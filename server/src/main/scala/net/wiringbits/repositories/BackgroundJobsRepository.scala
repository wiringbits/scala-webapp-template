package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.BackgroundJobDAO
import net.wiringbits.repositories.models.BackgroundJobData
import play.api.db.Database

import java.time.{Clock, Instant}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.control.NonFatal

class BackgroundJobsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {
  def streamPendingJobs: Future[akka.stream.scaladsl.Source[BackgroundJobData, Future[Int]]] = Future {
    // autocommit=false is necessary to avoid loading the whole result into memory
    implicit val conn = database.getConnection(autocommit = false)
    try {
      val stream = BackgroundJobDAO.streamPendingJobs()

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

  def setStatusToFailed(backgroundJobId: UUID, executeAt: Instant, failReason: String): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      BackgroundJobDAO.setStatusToFailed(backgroundJobId, executeAt, failReason)
    }
  }

  def setStatusToSuccess(backgroundJobId: UUID): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      BackgroundJobDAO.setStatusToSuccess(backgroundJobId)
    }
  }
}
