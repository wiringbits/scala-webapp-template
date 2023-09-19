package net.wiringbits.repositories

import anorm.*
import net.wiringbits.common.models.InstantCustom
import net.wiringbits.common.models.enums.BackgroundJobStatus
import net.wiringbits.common.models.id.BackgroundJobId
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.typo_generated.public.background_jobs.{BackgroundJobsRepoImpl, BackgroundJobsRow}
import play.api.db.Database

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.control.NonFatal

class BackgroundJobsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {
  def create(backgroundJobsRow: BackgroundJobsRow): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      BackgroundJobsRepoImpl.insert(backgroundJobsRow)
    }
  }

  def streamPendingJobs(
      allowedErrors: Int = 10,
      fetchSize: Int = 1000
  ): Future[akka.stream.scaladsl.Source[BackgroundJobsRow, Future[Int]]] = Future {
    // autocommit=false is necessary to avoid loading the whole result into memory
    implicit val conn = database.getConnection(autocommit = false)
    try {
      // TODO: use typo
      val query =
        SQL"""
        SELECT background_job_id, type, payload, status, status_details, error_count, execute_at, created_at, updated_at
        FROM background_jobs
        WHERE status != ${BackgroundJobStatus.Success.entryName}
          AND execute_at <= ${clock.instant()}
          AND error_count < $allowedErrors
        ORDER BY execute_at, background_job_id
        """.withFetchSize(Some(fetchSize)) // without this, all data is loaded into memory

      // this requires a Materializer that isn't used, better to set a null instead of depend on a Materializer
      @SuppressWarnings(Array("org.wartremover.warts.Null"))
      val materializer = null
      val stream = AkkaStream.source(query, backgroundJobParser)(materializer, conn)

      // make sure to close the connection when it isn't required anymore
      stream.mapMaterializedValue { result =>
        result.onComplete { t =>
          conn.close()
          t
        }
        result
      }
    } catch {
      case NonFatal(ex) =>
        conn.close()
        throw new RuntimeException("Failed to stream pending background jobs", ex)
    }
  }

  def setStatusToFailed(
      backgroundJobId: BackgroundJobId,
      executeAt: InstantCustom,
      failReason: String
  ): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      BackgroundJobsRepoImpl.update
        .where(_.backgroundJobId === backgroundJobId)
        .setValue(_.status)(BackgroundJobStatus.Failed)
        .setValue(_.statusDetails)(Some(failReason))
        .setComputedValueFromRow(_.errorCount)(_.errorCount + 1)
        .setValue(_.executeAt)(executeAt)
        .setValue(_.updatedAt)(InstantCustom.now())
        .execute()
    }
  }

  def setStatusToSuccess(backgroundJobId: BackgroundJobId): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      BackgroundJobsRepoImpl.update
        .where(_.backgroundJobId === backgroundJobId)
        .setValue(_.status)(BackgroundJobStatus.Success)
        .setValue(_.updatedAt)(InstantCustom.fromClock)
        .execute()
    }
  }
}
