package net.wiringbits.util

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.{UserToken, UserTokenType}
import net.wiringbits.typo_generated.customtypes.{TypoJsonb, TypoOffsetDateTime, TypoUUID}
import net.wiringbits.typo_generated.public.background_jobs.{
  BackgroundJobsId,
  BackgroundJobsRepoImpl,
  BackgroundJobsRow
}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRepoImpl, UserTokensRow}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import play.api.db.Database
import play.api.libs.json.Json

import java.sql.Connection
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EmailsHelper @Inject() (
    emailApi: EmailApi,
    webAppConfig: WebAppConfig,
    userTokensRepository: UserTokensRepository,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    clock: Clock,
    database: Database
)(implicit ec: ExecutionContext) {
  given c: Connection = database.getConnection()

  def sendEmailVerificationToken(user: UsersRow): Future[Instant] = {
    // we can't retrieve the plain text token, hence, we generate another one
    val token = tokenGenerator.next()
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)

    val createToken = UserTokensRow(
      userTokenId = UserTokensId(TypoUUID.randomUUID),
      token = hmacToken,
      tokenType = UserTokenType.EmailVerification.toString,
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      expiresAt = TypoOffsetDateTime(
        clock.instant().atOffset(ZoneOffset.UTC).plusSeconds(userTokensConfig.emailVerificationExp.toSeconds)
      ),
      userId = user.userId
    )

    for {
      _ <- Future(UserTokensRepoImpl.insert(createToken))
      _ <- sendRegistrationEmailWithVerificationToken(user, token)
    } yield createToken.expiresAt.value.toInstant
  }

  // we don't save emails in the queue when user tokens are involved
  def sendRegistrationEmailWithVerificationToken(user: UsersRow, token: UUID): Future[Unit] = {
    val emailParameter = s"${user.userId.value.value}_$token"
    val emailMessage = EmailMessage.registration(
      name = Name.trusted(user.name),
      url = webAppConfig.host,
      emailParameter = emailParameter
    )

    val request = EmailRequest(Email.trusted(user.email.value), emailMessage)
    emailApi.sendEmail(request)
  }

  // we don't save emails in the queue when user tokens are involved
  def sendPasswordRecoveryEmail(user: UsersRow): Future[Unit] = {
    val token = tokenGenerator.next()
    val emailParameter = s"${user.userId.value.value}_$token"
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    val createToken = UserToken
      .Create(
        id = UUID.randomUUID(),
        token = hmacToken,
        tokenType = UserTokenType.ResetPassword,
        createdAt = Instant.now(clock),
        userId = user.userId.value.value,
        expiresAt = Instant.now(clock).plus(userTokensConfig.resetPasswordExp.toHours, ChronoUnit.HOURS)
      )
    val message = EmailMessage.forgotPassword(Name.trusted(user.name), webAppConfig.host, emailParameter)

    for {
      _ <- userTokensRepository.create(createToken)
      _ <- emailApi.sendEmail(EmailRequest(Email.trusted(user.email.value), message))
    } yield ()
  }
}

object EmailsHelper {
  def sendEmailLater(userId: UsersId, emailMessage: EmailMessage)(using
      ec: ExecutionContext,
      conn: Connection,
      clock: Clock
  ): Future[Unit] = for {
    userOpt <- Future(UsersRepoImpl.selectById(userId))
    _ = userOpt.foreach { user =>
      val payload = BackgroundJobPayload.SendEmail(
        email = Email.trusted(user.email.value),
        subject = emailMessage.subject,
        body = emailMessage.body
      )

      val createNotification = BackgroundJobsRow(
        backgroundJobId = BackgroundJobsId(TypoUUID(UUID.randomUUID())),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(payload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = None,
        executeAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
        createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
        updatedAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
      )

      BackgroundJobsRepoImpl.insert(createNotification)
    }
  } yield ()
}
