package net.wiringbits.repositories.daos

import anorm.postgresql._
import net.wiringbits.models.jobs.BackgroundJobStatus
import net.wiringbits.repositories.models.BackgroundJobData
import play.api.libs.json.Json

import java.sql.Connection
import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.Future

object BackgroundJobDAO {

  import anorm._

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

  def streamPendingJobs(
      allowedErrors: Int = 10,
      fetchSize: Int = 1000
  )(implicit conn: Connection, clock: Clock): akka.stream.scaladsl.Source[BackgroundJobData, Future[Int]] = {
    val query = SQL"""
      SELECT background_job_id, type, payload, status, status_details, error_count, execute_at, created_at, updated_at
      FROM background_jobs
      WHERE status != ${BackgroundJobStatus.Success.toString}
        AND execute_at <= ${clock.instant()}
        AND error_count < $allowedErrors
      ORDER BY execute_at, background_job_id
      """.withFetchSize(Some(fetchSize)) // without this, all data is loaded into memory

    // this requires a Materializer that isn't used, better to set a null instead of depend on a Materializer
    @SuppressWarnings(Array("org.wartremover.warts.Null"))
    val materializer = null
    AkkaStream.source(query, backgroundJobParser)(materializer, conn)
  }

  def setStatusToFailed(backgroundJobId: UUID, executeAt: Instant, failReason: String)(implicit
      conn: Connection
  ): Unit = {
    val _ = SQL"""
      UPDATE background_jobs SET
        status = ${BackgroundJobStatus.Failed.toString}::TEXT,
        status_details = $failReason,
        error_count = error_count + 1,
        execute_at = $executeAt::TIMESTAMPTZ,
        updated_at = ${Instant.now()}::TIMESTAMPTZ
      WHERE background_job_id = ${backgroundJobId.toString}::UUID
      """
      .execute()
  }

  def setStatusToSuccess(backgroundJobId: UUID)(implicit
      conn: Connection
  ): Unit = {
    val _ = SQL"""
      UPDATE background_jobs SET
        status = ${BackgroundJobStatus.Success.toString}::TEXT,
        updated_at = ${Instant.now()}
      WHERE background_job_id = ${backgroundJobId.toString}::UUID
      """
      .execute()
  }
}
