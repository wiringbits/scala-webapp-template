package net.wiringbits.util

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.{User, UserToken, UserTokenType}

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
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

  def sendEmailVerificationToken(user: User): Future[Instant] = {
    // we can't retrieve the plain text token, hence, we generate another one
    val token = tokenGenerator.next()
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)

    val createToken = UserToken
      .Create(
        id = UUID.randomUUID(),
        token = hmacToken,
        tokenType = UserTokenType.EmailVerification,
        createdAt = Instant.now(clock),
        userId = user.id,
        expiresAt = Instant.now(clock).plus(userTokensConfig.emailVerificationExp.toSeconds, ChronoUnit.SECONDS)
      )

    for {
      _ <- userTokensRepository.create(createToken)
      _ <- sendRegistrationEmailWithVerificationToken(user, token)
    } yield createToken.expiresAt
  }

  // we don't save emails in the queue when user tokens are involved
  def sendRegistrationEmailWithVerificationToken(user: User, token: UUID): Future[Unit] = {
    val emailParameter = s"${user.id}_$token"
    val emailMessage = EmailMessage.registration(
      name = user.name,
      url = webAppConfig.host,
      emailParameter = emailParameter
    )

    val request = EmailRequest(user.email, emailMessage)
    emailApi.sendEmail(request)
  }

  // we don't save emails in the queue when user tokens are involved
  def sendPasswordRecoveryEmail(user: User): Future[Unit] = {
    val token = tokenGenerator.next()
    val emailParameter = s"${user.id}_$token"
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    val createToken = UserToken
      .Create(
        id = UUID.randomUUID(),
        token = hmacToken,
        tokenType = UserTokenType.ResetPassword,
        createdAt = Instant.now(clock),
        userId = user.id,
        expiresAt = Instant.now(clock).plus(userTokensConfig.resetPasswordExp.toHours, ChronoUnit.HOURS)
      )
    val message = EmailMessage.forgotPassword(user.name, webAppConfig.host, emailParameter)

    for {
      _ <- userTokensRepository.create(createToken)
      _ <- emailApi.sendEmail(EmailRequest(user.email, message))
    } yield ()
  }
}
