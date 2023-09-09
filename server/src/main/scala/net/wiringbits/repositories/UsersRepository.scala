package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.daos.BackgroundJobDAO
import net.wiringbits.repositories.models.*
import net.wiringbits.typo_generated.customtypes.{TypoJsonb, TypoOffsetDateTime, TypoUUID, TypoUnknownCitext}
import net.wiringbits.typo_generated.public.background_jobs.{
  BackgroundJobsId,
  BackgroundJobsRepoImpl,
  BackgroundJobsRow
}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRepoImpl, UserTokensRow}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import net.wiringbits.util.EmailMessage
import play.api.db.Database
import play.api.libs.json.Json

import java.sql.Connection
import java.time.temporal.ChronoUnit
import java.time.{Clock, ZoneOffset}
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

  def create(usersRow: UsersRow, verifyEmailToken: String): Future[Unit] = Future {
    val createUserTokensRow = UserTokensRow(
      userTokenId = UserTokensId(TypoUUID.randomUUID),
      token = verifyEmailToken,
      tokenType = UserTokenType.EmailVerification.toString,
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      expiresAt = TypoOffsetDateTime(
        clock.instant().plus(userTokensConfig.emailVerificationExp.toHours, ChronoUnit.HOURS).atOffset(ZoneOffset.UTC)
      ),
      userId = usersRow.userId
    )

    val createUserLogsRow = UserLogsRow(
      userLogId = UserLogsId(TypoUUID.randomUUID),
      userId = usersRow.userId,
      message = s"Account created, name = ${usersRow.name}, email = ${usersRow.email}",
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
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
      UsersRepoImpl.select.where(_.email === TypoUnknownCitext(email.string)).limit(1).toList.headOption
    }
  }

  def find(usersId: UsersId): Future[Option[UsersRow]] = Future {
    database.withConnection { implicit conn =>
      UsersRepoImpl.selectById(usersId)
    }
  }

  def update(usersRow: UsersRow, name: Name): Future[Unit] = Future {
    val updatedUsersRow = usersRow.copy(name = name.string)

    val createUserLogsRow = UserLogsRow(
      userLogId = UserLogsId(TypoUUID.randomUUID),
      userId = usersRow.userId,
      message = s"Profile updated",
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update(updatedUsersRow)
      UserLogsRepoImpl.insert(createUserLogsRow)
    }
  }

  def updatePassword(usersRow: UsersRow, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    val updatedUsersRow = usersRow.copy(password = password)

    val createUserLogsRow = UserLogsRow(
      userLogId = UserLogsId(TypoUUID.randomUUID),
      userId = usersRow.userId,
      message = s"Password updated",
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.insert(updatedUsersRow)
      UserLogsRepoImpl.insert(createUserLogsRow)
      sendEmailLater(usersRow, emailMessage)
    }
  }

  def verify(usersRow: UsersRow, userTokensId: UserTokensId, emailMessage: EmailMessage): Future[Unit] = Future {
    val updateUsersRow = usersRow.copy(verifiedOn = Some(TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))))

    val createUserLogsRow = UserLogsRow(
      userLogId = UserLogsId(TypoUUID.randomUUID),
      userId = usersRow.userId,
      message = s"Email verified",
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update(updateUsersRow)
      UserLogsRepoImpl.insert(createUserLogsRow)
      UserTokensRepoImpl.delete
        .where(_.userTokenId === userTokensId)
        .where(_.userId === usersRow.userId)
        .execute()
      sendEmailLater(usersRow, emailMessage)
    }
  }

  def resetPassword(usersRow: UsersRow, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    val updatedUsersRow = usersRow.copy(password = password)

    val createUserLogsRow = UserLogsRow(
      userLogId = UserLogsId(TypoUUID.randomUUID),
      userId = usersRow.userId,
      message = s"Password reset",
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update(updatedUsersRow)
      UserLogsRepoImpl.insert(createUserLogsRow)
      sendEmailLater(usersRow, emailMessage)
    }
  }

  private def sendEmailLater(usersRow: UsersRow, emailMessage: EmailMessage)(implicit conn: Connection): Unit = {
    val payload = BackgroundJobPayload.SendEmail(
      email = Email.trusted(usersRow.email.value),
      subject = emailMessage.subject,
      body = emailMessage.body
    )

    val backgroundJobDatasRow = BackgroundJobsRow(
      backgroundJobId = BackgroundJobsId(TypoUUID.randomUUID),
      `type` = BackgroundJobType.SendEmail.toString,
      payload = TypoJsonb(Json.toJson(payload).toString),
      status = BackgroundJobStatus.Pending.toString,
      statusDetails = None,
      errorCount = None,
      executeAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      updatedAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    BackgroundJobsRepoImpl.insert(backgroundJobDatasRow)
  }
}
