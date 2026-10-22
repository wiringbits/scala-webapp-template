package net.wiringbits.repositories.daos

import anorm.postgresql.*
import net.wiringbits.models.jobs.{BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.models.BackgroundJobData
import play.api.libs.json.Json

import java.sql.Connection
import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.duration.FiniteDuration

object BackgroundJobDAO {

  import anorm.*

  def create(request: BackgroundJobData.Create)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      INSERT INTO background_jobs
        (background_job_id, type, payload, status, execute_at, created_at, updated_at)
      VALUES (
        ${request.id},
        ${request.`type`.toString},
        ${Json.toJson(request.payload)},
        ${request.status.toString},
        ${request.executeAt},
        ${request.createdAt},
        ${request.updatedAt}
      )
      """
      .execute()
  }

  /** Atomically claims up to `limit` ready jobs of the given type, marking them Running until `now + timeout`.
    *
    * A job is ready when it is Pending/Failed and its execute_at has passed, or when it is Running but its deadline
    * expired (the previous claim was abandoned); reclaiming an expired claim counts as an error. Rows locked by a
    * concurrent claim are skipped, so concurrent callers always get disjoint jobs.
    */
  def claimJobs(
      jobType: BackgroundJobType,
      timeout: FiniteDuration,
      limit: Int,
      allowedErrors: Int = 10
  )(implicit conn: Connection, clock: Clock): List[BackgroundJobData] = {
    val now = clock.instant()
    val deadline = now.plusMillis(timeout.toMillis)
    SQL"""
      UPDATE background_jobs SET
        status = ${BackgroundJobStatus.Running.toString}::TEXT,
        deadline = $deadline::TIMESTAMPTZ,
        error_count = error_count + (CASE WHEN status = ${BackgroundJobStatus.Running.toString}::TEXT THEN 1 ELSE 0 END),
        updated_at = $now::TIMESTAMPTZ
      WHERE background_job_id IN (
        SELECT background_job_id
        FROM background_jobs
        WHERE type = ${jobType.toString}::TEXT
          AND error_count < $allowedErrors
          AND (
            (
              status IN (${BackgroundJobStatus.Pending.toString}::TEXT, ${BackgroundJobStatus.Failed.toString}::TEXT)
              AND execute_at <= $now::TIMESTAMPTZ
            )
            OR (status = ${BackgroundJobStatus.Running.toString}::TEXT AND deadline <= $now::TIMESTAMPTZ)
          )
        ORDER BY execute_at, background_job_id
        LIMIT $limit
        FOR UPDATE SKIP LOCKED
      )
      RETURNING background_job_id, type, payload, status, status_details, error_count, execute_at, deadline, created_at, updated_at
      """.as(backgroundJobParser.*)
  }

  def setStatusToFailed(backgroundJobId: UUID, executeAt: Instant, failReason: String)(implicit
      conn: Connection
  ): Unit = {
    val affected = SQL"""
      UPDATE background_jobs SET
        status = ${BackgroundJobStatus.Failed.toString}::TEXT,
        status_details = $failReason,
        error_count = error_count + 1,
        execute_at = $executeAt::TIMESTAMPTZ,
        updated_at = ${Instant.now()}::TIMESTAMPTZ
      WHERE background_job_id = ${backgroundJobId.toString}::UUID
      """
      .executeUpdate()
    if (affected == 0) throw new RuntimeException(s"Background job not found: $backgroundJobId")
  }

  def setStatusToSuccess(backgroundJobId: UUID)(implicit
      conn: Connection
  ): Unit = {
    val affected = SQL"""
      UPDATE background_jobs SET
        status = ${BackgroundJobStatus.Success.toString}::TEXT,
        updated_at = ${Instant.now()}
      WHERE background_job_id = ${backgroundJobId.toString}::UUID
      """
      .executeUpdate()
    if (affected == 0) throw new RuntimeException(s"Background job not found: $backgroundJobId")
  }
}
