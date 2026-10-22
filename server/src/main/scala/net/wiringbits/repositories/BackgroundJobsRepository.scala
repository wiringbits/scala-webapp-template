package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.models.jobs.BackgroundJobType
import net.wiringbits.repositories.daos.BackgroundJobDAO
import net.wiringbits.repositories.models.BackgroundJobData
import play.api.db.Database

import java.time.{Clock, Instant}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class BackgroundJobsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {
  def claimJobs(jobType: BackgroundJobType, timeout: FiniteDuration, limit: Int): Future[List[BackgroundJobData]] =
    Future {
      database.withTransaction { implicit conn =>
        BackgroundJobDAO.claimJobs(jobType, timeout, limit)
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
