package net.wiringbits.repositories

import anorm.{AkkaStream, SqlStringInterpolation}
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.models.jobs.BackgroundJobStatus
import net.wiringbits.typo_generated.customtypes.TypoOffsetDateTime
import net.wiringbits.typo_generated.public.background_jobs.{BackgroundJobsRepoImpl, BackgroundJobsRow}
import play.api.db.Database

import java.time.{Clock, Instant, ZoneOffset}
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.control.NonFatal

class BackgroundJobsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {
  def streamPendingJobs: Future[akka.stream.scaladsl.Source[BackgroundJobsRow, Future[Int]]] = Future {
    // autocommit=false is necessary to avoid loading the whole result into memory
    implicit val conn = database.getConnection(autocommit = false)
    try {
      // TODO: use Typo
      def streamPendingJobs(allowedErrors: Int = 10, fetchSize: Int = 1000) = {
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
        AkkaStream.source(query, BackgroundJobsRow.rowParser(0))(materializer, conn)
      }

      val stream = streamPendingJobs()

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
      backgroundJobsRow: BackgroundJobsRow,
      executeAt: TypoOffsetDateTime,
      failReason: String
  ): Future[Unit] = Future {
    val updatedBackgroundJobsRow = backgroundJobsRow.copy(
      status = BackgroundJobStatus.Failed.toString,
      statusDetails = Some(failReason),
      errorCount = backgroundJobsRow.errorCount.map(_ + 1),
      executeAt = executeAt,
      updatedAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withConnection { implicit conn =>
      BackgroundJobsRepoImpl.update(updatedBackgroundJobsRow)
    }
  }

  def setStatusToSuccess(backgroundJobsRow: BackgroundJobsRow): Future[Unit] = Future {
    val updatedBackgroundJobsRow = backgroundJobsRow.copy(
      status = BackgroundJobStatus.Success.toString,
      updatedAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withConnection { implicit conn =>
      BackgroundJobsRepoImpl.update(updatedBackgroundJobsRow)
    }
  }
}
