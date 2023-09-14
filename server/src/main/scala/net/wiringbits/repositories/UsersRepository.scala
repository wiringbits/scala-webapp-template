package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
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
      userTokenId = UUID.randomUUID(),
      token = verifyEmailToken,
      tokenType = UserTokenType.EmailVerification.toString,
      createdAt = clock.instant(),
      expiresAt = clock.instant().plus(userTokensConfig.emailVerificationExp.toHours, ChronoUnit.HOURS),
      userId = usersRow.userId
    )

    val createUserLogsRow = UserLogsRow(
      userLogId = UUID.randomUUID(),
      userId = usersRow.userId,
      message = s"Account created, name = ${usersRow.name}, email = ${usersRow.email}",
      createdAt = clock.instant()
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
        .limit(1)
        .toList
        .headOption
    }
  }

  def find(userId: UUID): Future[Option[UsersRow]] = Future {
    database.withConnection { implicit conn =>
//      UsersRepoImpl.selectById(userId)
      println(UsersRepoImpl.select.where(_.userId === userId).sql)
      UsersRepoImpl.select.where(_.userId === userId).toList.headOption
    }
  }

  def update(userId: UUID, name: Name): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUID.randomUUID(),
      userId = userId,
      message = s"Profile updated",
      createdAt = clock.instant()
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update.where(_.userId === userId).setValue(_.name)(name).execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
    }
  }

  def updatePassword(userId: UUID, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUID.randomUUID(),
      userId = userId,
      message = s"Password updated",
      createdAt = clock.instant()
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update.where(_.userId === userId).setValue(_.password)(password).execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
      sendEmailLater(userId, emailMessage)
    }
  }

  def verify(userId: UUID, userTokenId: UUID, emailMessage: EmailMessage): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUID.randomUUID(),
      userId = userId,
      message = s"Email verified",
      createdAt = clock.instant()
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update
        .where(_.userId === userId)
        .setValue(_.verifiedOn)(Some(clock.instant()))
        .execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
      UserTokensRepoImpl.delete
        .where(_.userTokenId === userTokenId)
        .where(_.userId === userId)
        .execute()
      sendEmailLater(userId, emailMessage)
    }
  }

  def resetPassword(userId: UUID, password: String, emailMessage: EmailMessage): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUID.randomUUID(),
      userId = userId,
      message = s"Password reset",
      createdAt = clock.instant()
    )

    database.withTransaction { implicit conn =>
      UsersRepoImpl.update.where(_.userId === userId).setValue(_.password)(password).execute()
      UserLogsRepoImpl.insert(createUserLogsRow)
      sendEmailLater(userId, emailMessage)
    }
  }

  private def sendEmailLater(userId: UUID, emailMessage: EmailMessage)(implicit conn: Connection): Unit = {
    val usersRow = UsersRepoImpl.selectById(userId)
    usersRow.foreach { usersRow =>
      val payload = BackgroundJobPayload.SendEmail(
        email = usersRow.email,
        subject = emailMessage.subject,
        body = emailMessage.body
      )

      val backgroundJobDatasRow = BackgroundJobsRow(
        backgroundJobId = UUID.randomUUID(),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(payload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = None,
        executeAt = clock.instant(),
        createdAt = clock.instant(),
        updatedAt = clock.instant()
      )

      BackgroundJobsRepoImpl.insert(backgroundJobDatasRow)
    }
  }
}
