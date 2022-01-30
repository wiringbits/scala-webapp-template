package net.wiringbits.actions

import net.wiringbits.api.models.ForgotPassword
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories.models.{User, UserToken, UserTokenType}
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.util.{EmailMessage, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateCaptcha, ValidateVerifiedUser}

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ForgotPasswordAction @Inject() (
    userTokensConfig: UserTokensConfig,
    webAppConfig: WebAppConfig,
    captchaApi: ReCaptchaApi,
    usersRepository: UsersRepository,
    emailApi: EmailApi,
    userTokensRepository: UserTokensRepository,
    tokenGenerator: TokenGenerator
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  def apply(request: ForgotPassword.Request): Future[ForgotPassword.Response] = {
    for {
      _ <- ValidateCaptcha(captchaApi, request.captcha)
      userMaybe <- usersRepository.find(request.email)

      // submit the email only when the user exists, otherwise, ignore the request
      _ <- userMaybe.map(whenExists).getOrElse(Future.unit)
    } yield ForgotPassword.Response()
  }

  private def whenExists(user: User) = {
    val token = tokenGenerator.next()
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

    ValidateVerifiedUser(user)
    val emailMessage = EmailMessage.forgotPassword(user.name, webAppConfig.host, s"${user.id}_$token")
    for {
      _ <- userTokensRepository.create(createToken)
      _ <- emailApi.sendEmail(EmailRequest(user.email, emailMessage))
    } yield ()
  }
}
