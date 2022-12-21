package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.daos.{BackgroundJobDAO, UserLogsDAO, UserTokensDAO, UsersDAO}
import net.wiringbits.repositories.models._
import net.wiringbits.util.EmailMessage
import play.api.db.Database

import java.sql.Connection
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UsersRepository @Inject() (
    database: Database,
    userTokensConfig: UserTokensConfig
)(implicit
    ec: DatabaseExecutionContext,
    clock: Clock
) {

  def create(request: User.CreateUser): Future[Unit] = Future {
    val createToken = UserToken.Create(
      id = UUID.randomUUID(),
      token = request.verifyEmailToken,
      tokenType = UserTokenType.EmailVerification,
      createdAt = clock.instant(),
      expiresAt = clock.instant().plus(userTokensConfig.emailVerificationExp.toHours, ChronoUnit.HOURS),
      userId = request.id
    )

    database.withTransaction { implicit conn =>
      UsersDAO.create(request)
      UserTokensDAO.create(createToken)
      UserLogsDAO.create(
        UserLog.CreateUserLog(
          UUID.randomUUID(),
          request.id,
          s"Account created, name = ${request.name}, email = ${request.email}"
        )
      )
    }
  }

  def all(): Future[List[User]] = Future {
    database.withConnection { implicit conn =>
      UsersDAO.all()
    }
  }

  def find(email: Email): Future[Option[User]] = Future {
    database.withConnection { implicit conn =>
      UsersDAO.find(email)
    }
  }

  def find(userId: UUID): Future[Option[User]] = Future {
    database.withConnection { implicit conn =>
      UsersDAO.find(userId)
    }
  }

  def update(userId: UUID, name: Name): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      UsersDAO.updateName(userId, name)
      UserLogsDAO.create(
        UserLog.CreateUserLog(
          UUID.randomUUID(),
          userId = userId,
          "Profile updated"
        )
      )
    }
  }

  def updatePassword(userId: UUID, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      UsersDAO.resetPassword(userId, password)
      val request = UserLog.CreateUserLog(UUID.randomUUID(), userId, "Password was updated")
      UserLogsDAO.create(request)
      sendEmailLater(userId, emailMessage)
    }
  }

  def verify(userId: UUID, tokenId: UUID, emailMessage: EmailMessage): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      UsersDAO.verify(userId)
      UserLogsDAO.create(
        UserLog.CreateUserLog(
          UUID.randomUUID(),
          userId = userId,
          "Email verified"
        )
      )
      UserTokensDAO.delete(tokenId = tokenId, userId = userId)
      sendEmailLater(userId, emailMessage)
    }
  }

  def resetPassword(userId: UUID, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      UsersDAO.resetPassword(userId, password)
      val request = UserLog.CreateUserLog(UUID.randomUUID(), userId, "Password was reset")
      UserLogsDAO.create(request)
      sendEmailLater(userId, emailMessage)
    }
  }

  private def sendEmailLater(userId: UUID, emailMessage: EmailMessage)(implicit conn: Connection): Unit = {
    val userOpt = UsersDAO.find(userId)
    userOpt.foreach { user =>
      val payload = BackgroundJobPayload.SendEmail(
        email = user.email,
        subject = emailMessage.subject,
        body = emailMessage.body
      )
      val createNotification = BackgroundJobData.Create(
        id = UUID.randomUUID(),
        `type` = BackgroundJobType.SendEmail,
        payload = payload,
        status = BackgroundJobStatus.Pending,
        executeAt = clock.instant(),
        createdAt = clock.instant(),
        updatedAt = clock.instant()
      )

      BackgroundJobDAO.create(createNotification)
    }
  }
}
