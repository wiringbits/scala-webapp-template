package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.UserLogsDAO
import net.wiringbits.repositories.models.UserLog
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UserLogsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext) {

  def create(request: UserLog.CreateUserLog): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserLogsDAO.create(request)
    }
  }

  def create(userId: UUID, message: String): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      val request = UserLog.CreateUserLog(UUID.randomUUID(), userId, message)
      UserLogsDAO.create(request)
    }
  }

  def logs(userId: UUID): Future[List[UserLog]] = Future {
    database.withConnection { implicit conn =>
      UserLogsDAO.logs(userId)
    }
  }
}
