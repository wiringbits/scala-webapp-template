package net.wiringbits.actions

import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi}
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.{EmailMessage, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateEmailIsRegistered}
import net.wiringbits.repositories.models.{UserToken, UserTokenType}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import net.wiringbits.api.models.SendVerifyEmail
import net.wiringbits.repositories.UserTokensRepository
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import java.util.UUID

class SendVerifyEmailAction @Inject() (
    usersRepository: UsersRepository,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    userTokensRepository: UserTokensRepository,
    webAppConfig: WebAppConfig,
    emailApi: EmailApi
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(request: SendVerifyEmail.Request): Future[SendVerifyEmail.Response] = {
    for {
      _ <- validations(request)

      userMaybe <- usersRepository.find(request.email)
      user = userMaybe.getOrElse(throw new RuntimeException(s"User with email ${request.email} wasn't found"))

      token = tokenGenerator.next()
      hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)
      userTokens <- userTokensRepository.find(user.id)
      _ = userTokens.foreach { tokenRegistered => userTokensRepository.delete(tokenRegistered.id, user.id) }
      createToken = UserToken
      .Create(
        id = UUID.randomUUID(),
        token = hmacToken,
        tokenType = UserTokenType.EmailVerification,
        createdAt = Instant.now(clock),
        userId = user.id,
        expiresAt = Instant.now(clock).plus(userTokensConfig.resetPasswordExp.toHours, ChronoUnit.HOURS)
      )
      _ <- userTokensRepository.create(createToken)

      emailParameter = s"${user.id}_$token"
      emailMessage = EmailMessage.registration(
        name = user.name,
        url = webAppConfig.host,
        emailParameter = emailParameter
      )
      _ <- emailApi.sendEmail(EmailRequest(request.email, emailMessage))
    } yield SendVerifyEmail.Response(message = "Email sent to verify.")
  }

  private def validations(request: SendVerifyEmail.Request) = {
    for {
      _ <- ValidateEmailIsRegistered(usersRepository, request.email)
    } yield ()
  }
}
