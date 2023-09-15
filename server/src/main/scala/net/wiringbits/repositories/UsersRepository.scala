package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, InstantCustom, Name, UUIDCustom}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.models.*
import net.wiringbits.typo_generated.customtypes.TypoJsonb
import net.wiringbits.typo_generated.public.background_jobs.{BackgroundJobsRepoImpl, BackgroundJobsRow}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensRepoImpl, UserTokensRow}
import net.wiringbits.typo_generated.public.users.{UsersRepoImpl, UsersRow}
import net.wiringbits.util.EmailMessage
import play.api.db.Database
import play.api.libs.json.Json

import java.sql.Connection
import java.time.Clock
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.Future

class UsersRepository @Inject() (
    database: Database,
    userTokensConfig: UserTokensConfig
)(implicit
    ec: DatabaseExecutionContext,
    clock: Clock
) {

  def create(usersRow: UsersRow, verifyEmailToken: String): Future[Unit] = Future {
    val createUserTokensRow = UserTokensRow(
      userTokenId = UUIDCustom.randomUUID(),
      token = verifyEmailToken,
      tokenType = UserTokenType.EmailVerification.toString,
      createdAt = InstantCustom.fromClock,
      expiresAt = InstantCustom.fromClock.plus(userTokensConfig.emailVerificationExp.toHours, ChronoUnit.HOURS),
      userId = usersRow.userId
    )

    val createUserLogsRow = UserLogsRow(
      userLogId = UUIDCustom.randomUUID(),
      userId = usersRow.userId,
      message = s"Account created, name = ${usersRow.name}, email = ${usersRow.email}",
      createdAt = InstantCustom.fromClock
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.insert(usersRow)
      UserTokensRepoImpl.insert(createUserTokensRow)
      UserLogsRepoImpl.insert(createUserLogsRow)
    }
  }

  def all(): Future[List[UsersRow]] = Future {
    database.withConnection { implicit conn =>
      UsersRepoImpl.selectAll
    }
  }

  def find(email: Email): Future[Option[UsersRow]] = Future {
    database.withConnection { implicit conn =>
      UsersRepoImpl.select
        .where(_.email === email)
        .orderBy(_.createdAt.desc)
        .toList
        .headOption
    }
  }

  def find(userId: UUIDCustom): Future[Option[UsersRow]] = Future {
    database.withConnection { implicit conn =>
      UsersRepoImpl.selectById(userId)
    }
  }

  def update(userId: UUIDCustom, name: Name): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUIDCustom.randomUUID(),
      userId = userId,
      message = s"Profile updated",
      createdAt = InstantCustom.fromClock
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update.where(_.userId === userId).setValue(_.name)(name).execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
    }
  }

  def updatePassword(userId: UUIDCustom, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUIDCustom.randomUUID(),
      userId = userId,
      message = s"Password updated",
      createdAt = InstantCustom.fromClock
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update.where(_.userId === userId).setValue(_.password)(password).execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
      sendEmailLater(userId, emailMessage)
    }
  }

  def verify(userId: UUIDCustom, userTokenId: UUIDCustom, emailMessage: EmailMessage): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUIDCustom.randomUUID(),
      userId = userId,
      message = s"Email verified",
      createdAt = InstantCustom.fromClock
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update
        .where(_.userId === userId)
        .setValue(_.verifiedOn)(Some(InstantCustom.fromClock))
        .execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
      UserTokensRepoImpl.delete
        .where(_.userTokenId === userTokenId)
        .where(_.userId === userId)
        .execute()
      sendEmailLater(userId, emailMessage)
    }
  }

  def resetPassword(userId: UUIDCustom, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUIDCustom.randomUUID(),
      userId = userId,
      message = s"Password reset",
      createdAt = InstantCustom.fromClock
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update.where(_.userId === userId).setValue(_.password)(password).execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
      sendEmailLater(userId, emailMessage)
    }
  }

  private def sendEmailLater(userId: UUIDCustom, emailMessage: EmailMessage)(implicit conn: Connection): Unit = {
    val usersRow = UsersRepoImpl.selectById(userId)
    usersRow.foreach { usersRow =>
      val payload = BackgroundJobPayload.SendEmail(
        email = usersRow.email,
        subject = emailMessage.subject,
        body = emailMessage.body
      )

      val backgroundJobDatasRow = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(payload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = Some(0),
        executeAt = InstantCustom.fromClock,
        createdAt = InstantCustom.fromClock,
        updatedAt = InstantCustom.fromClock
      )

      BackgroundJobsRepoImpl.insert(backgroundJobDatasRow)
    }
  }
}
