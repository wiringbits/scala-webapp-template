package net.wiringbits.util

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.UserTokenType
import net.wiringbits.typo_generated.public.user_tokens.UserTokensRow
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
      userTokenId = UUID.randomUUID(),
      token = hmacToken,
      tokenType = UserTokenType.EmailVerification.toString,
      createdAt = clock.instant(),
      expiresAt = clock.instant().plusSeconds(userTokensConfig.emailVerificationExp.toSeconds),
      userId = user.userId
    )

    for {
      _ <- userTokensRepository.create(createToken)
      _ <- sendRegistrationEmailWithVerificationToken(user, token)
    } yield createToken.expiresAt
  }

  // we don't save emails in the queue when user tokens are involved
  def sendRegistrationEmailWithVerificationToken(user: UsersRow, token: UUID): Future[Unit] = {
    val emailParameter = s"${user.userId}_$token"
    val emailMessage = EmailMessage.registration(
      name = user.name,
      url = webAppConfig.host,
      emailParameter = emailParameter
    )

    val request = EmailRequest(user.email, emailMessage)
    emailApi.sendEmail(request)
  }

  // we don't save emails in the queue when user tokens are involved
  def sendPasswordRecoveryEmail(usersRow: UsersRow): Future[Unit] = {
    val token = tokenGenerator.next()
    val emailParameter = s"${usersRow.userId}_$token"
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    val createUserTokensRow = UserTokensRow(
      userTokenId = UUID.randomUUID(),
      token = hmacToken,
      tokenType = UserTokenType.ResetPassword.toString,
      createdAt = clock.instant(),
      expiresAt = clock.instant().plus(userTokensConfig.resetPasswordExp.toHours, ChronoUnit.HOURS),
      userId = usersRow.userId
    )
    val message = EmailMessage.forgotPassword(usersRow.name, webAppConfig.host, emailParameter)

    for {
      _ <- userTokensRepository.create(createUserTokensRow)
      _ <- emailApi.sendEmail(EmailRequest(usersRow.email, message))
    } yield ()
  }
}
