package net.wiringbits.actions

import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.{EmailMessage, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateCaptcha, ValidateEmailIsRegistered, ValidateUserIsNotVerified}
import net.wiringbits.repositories.models.{UserToken, UserTokenType}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import net.wiringbits.api.models.SendEmailVerificationToken
import net.wiringbits.repositories.UserTokensRepository
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import java.util.UUID

class SendEmailVerificationTokenAction @Inject() (
    usersRepository: UsersRepository,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    userTokensRepository: UserTokensRepository,
    webAppConfig: WebAppConfig,
    emailApi: EmailApi,
    reCaptchaApi: ReCaptchaApi
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(request: SendEmailVerificationToken.Request): Future[SendEmailVerificationToken.Response] = {
    for {
      _ <- validations(request)
      userMaybe <- usersRepository.find(request.email)
      user = userMaybe.getOrElse(throw new RuntimeException(s"User with email ${request.email} wasn't found"))
      _ = ValidateUserIsNotVerified(user)

      token = tokenGenerator.next()
      hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)

      createToken = UserToken
        .Create(
          id = UUID.randomUUID(),
          token = hmacToken,
          tokenType = UserTokenType.EmailVerification,
          createdAt = Instant.now(clock),
          userId = user.id,
          expiresAt = Instant.now(clock).plus(userTokensConfig.emailVerificationExp.toSeconds, ChronoUnit.SECONDS)
        )
      _ <- userTokensRepository.create(createToken)

      emailParameter = s"${user.id}_$token"
      emailMessage = EmailMessage.registration(
        name = user.name,
        url = webAppConfig.host,
        emailParameter = emailParameter
      )
      _ <- emailApi.sendEmail(EmailRequest(request.email, emailMessage))
    } yield SendEmailVerificationToken.Response(expiresAt = createToken.expiresAt)
  }

  private def validations(request: SendEmailVerificationToken.Request) = {
    for {
      _ <- ValidateCaptcha(reCaptchaApi, request.captcha)
      _ <- ValidateEmailIsRegistered(usersRepository, request.email)
    } yield ()
  }
}
