package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.{UserNotificationsDAO, UserTokensDAO}
import net.wiringbits.repositories.models.{NotificationStatus, NotificationType, UserNotification, UserToken}
import net.wiringbits.util.EmailMessage
import play.api.db.Database

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UserTokensRepository @Inject() (
    database: Database
)(implicit
    ec: DatabaseExecutionContext,
    clock: Clock
) {

  def create(request: UserToken.Create, emailMessage: EmailMessage): Future[Unit] = Future {
    val createNotification = UserNotification.Create(
      id = UUID.randomUUID(),
      userId = request.userId,
      notificationType = NotificationType.ForgotPassword,
      subject = emailMessage.subject,
      message = emailMessage.body,
      status = NotificationStatus.Pending,
      executeAt = clock.instant()
    )

    database.withTransaction { implicit conn =>
      UserTokensDAO.create(request)
      UserNotificationsDAO.create(createNotification)
    }
  }

  def find(userId: UUID, token: String): Future[Option[UserToken]] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.find(userId, token)
    }
  }

  def find(userId: UUID): Future[List[UserToken]] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.find(userId)
    }
  }

  def delete(tokenId: UUID, userId: UUID): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.delete(tokenId, userId: UUID)
    }
  }
}
