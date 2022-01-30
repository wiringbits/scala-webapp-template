package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.UserNotificationsDAO
import net.wiringbits.repositories.models.UserNotification
import play.api.db.Database

import java.time.{Clock, Instant}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UserNotificationsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {
  def getPendingNotifications: Future[List[UserNotification]] = Future {
    database.withConnection { implicit conn =>
      UserNotificationsDAO.getPendingNotifications()
    }
  }

  def setStatusToFailed(notificationId: UUID, executeAt: Instant, failReason: String): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserNotificationsDAO.setStatusToFailed(notificationId, executeAt, failReason)
    }
  }

  def setStatusToSuccess(notificationId: UUID): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserNotificationsDAO.setStatusToSuccess(notificationId)
    }
  }

}
