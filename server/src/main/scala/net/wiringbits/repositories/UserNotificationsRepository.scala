package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.UserNotificationsDAO
import net.wiringbits.repositories.models.UserNotification
import play.api.db.Database

import java.time.{Clock, Instant}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.control.NonFatal

class UserNotificationsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {
  def streamPendingNotifications: Future[akka.stream.scaladsl.Source[UserNotification, Future[Int]]] = Future {
    // autocommit=false is necessary to avoid loading the whole result into memory
    implicit val conn = database.getConnection(autocommit = false)
    try {
      val stream = UserNotificationsDAO.streamPendingNotifications()

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
        throw new RuntimeException("Failed to stream pending notifications", ex)
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
