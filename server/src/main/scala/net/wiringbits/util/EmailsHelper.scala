package net.wiringbits.util

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.UserTokenType
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRow}
import net.wiringbits.typo_generated.public.users.UsersRow

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
    clock: Clock
)(implicit ec: ExecutionContext) {
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
      _ <- userTokensRepository.create(createToken)
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
  def sendPasswordRecoveryEmail(usersRow: UsersRow): Future[Unit] = {
    val token = tokenGenerator.next()
    val emailParameter = s"${usersRow.userId.value.value}_$token"
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    val createUserTokensRow = UserTokensRow(
      userTokenId = UserTokensId(TypoUUID.randomUUID),
      token = hmacToken,
      tokenType = UserTokenType.ResetPassword.toString,
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      expiresAt = TypoOffsetDateTime(
        clock.instant().plus(userTokensConfig.resetPasswordExp.toHours, ChronoUnit.HOURS).atOffset(ZoneOffset.UTC)
      ),
      userId = usersRow.userId
    )
    val message = EmailMessage.forgotPassword(Name.trusted(usersRow.name), webAppConfig.host, emailParameter)

    for {
      _ <- userTokensRepository.create(createUserTokensRow)
      _ <- emailApi.sendEmail(EmailRequest(Email.trusted(usersRow.email.value), message))
    } yield ()
  }
}
