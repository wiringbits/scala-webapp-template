package utils

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositoryComponents
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.daos.BackgroundJobDAO
import net.wiringbits.repositories.models.{BackgroundJobData, User, UserLog, UserToken, UserTokenType}
import org.scalatest.concurrent.ScalaFutures.*

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.annotation.unused
import scala.concurrent.{ExecutionContext, Future}

trait RepositoryUtils {
  val backgroundJobPayload: BackgroundJobPayload.SendEmail =
    BackgroundJobPayload.SendEmail(Email.trusted("sample@wiringbits.net"), subject = "Test message", body = "it works")

  def createBackgroundJobData(
      id: UUID = UUID.randomUUID(),
      backgroundJobType: BackgroundJobType = BackgroundJobType.SendEmail,
      status: BackgroundJobStatus = BackgroundJobStatus.Pending,
      payload: BackgroundJobPayload = backgroundJobPayload
  )(using repositories: RepositoryComponents): BackgroundJobData.Create = {
    val createRequest = BackgroundJobData.Create(
      id = id,
      `type` = backgroundJobType,
      payload = payload,
      status = status,
      executeAt = Instant.now(),
      createdAt = Instant.now(),
      updatedAt = Instant.now()
    )

    repositories.database.withConnection { implicit conn =>
      BackgroundJobDAO.create(createRequest)
    }

    createRequest
  }

  def createUser(
      email: Email = Email.trusted("hello@wiringbits.net")
  )(using repository: RepositoryComponents)(using @unused ec: ExecutionContext): Future[User.CreateUser] = {
    val createRequest = User.CreateUser(
      id = UUID.randomUUID(),
      email = email,
      name = Name.trusted("Sample"),
      hashedPassword = "password",
      verifyEmailToken = "token"
    )

    for {
      _ <- repository.users.create(createRequest)
    } yield createRequest
  }

  def createUserLog(
      userId: UUID
  )(using repository: RepositoryComponents)(using @unused ec: ExecutionContext): Future[UserLog.CreateUserLog] = {
    val createRequest =
      UserLog.CreateUserLog(userLogId = UUID.randomUUID(), userId = userId, message = "test")

    for {
      _ <- repository.userLogs.create(createRequest)
    } yield createRequest
  }

  def createUserLog(
      userId: UUID,
      message: String
  )(using repository: RepositoryComponents): Future[Unit] = {
    repository.userLogs.create(userId, message)
  }

  def createToken(
      userId: UUID
  )(using @unused ec: ExecutionContext, repository: RepositoryComponents): Future[UserToken.Create] = {
    val tokenRequest =
      UserToken.Create(
        id = UUID.randomUUID(),
        token = "test",
        tokenType = UserTokenType.ResetPassword,
        createdAt = Instant.now(),
        expiresAt = Instant.now.plus(2, ChronoUnit.DAYS),
        userId = userId
      )

    for {
      _ <- repository.userTokens.create(tokenRequest)
    } yield tokenRequest
  }
}
