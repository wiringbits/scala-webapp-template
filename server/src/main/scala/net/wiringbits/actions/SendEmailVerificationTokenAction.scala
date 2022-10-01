package net.wiringbits.actions

import net.wiringbits.api.models.SendEmailVerificationToken
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.EmailsHelper
import net.wiringbits.validations.{ValidateCaptcha, ValidateEmailIsRegistered, ValidateUserIsNotVerified}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SendEmailVerificationTokenAction @Inject() (
    usersRepository: UsersRepository,
    emailsHelper: EmailsHelper,
    reCaptchaApi: ReCaptchaApi
)(implicit ec: ExecutionContext) {

  def apply(request: SendEmailVerificationToken.Request): Future[SendEmailVerificationToken.Response] = {
    for {
      _ <- validations(request)
      userMaybe <- usersRepository.find(request.email)
      user = userMaybe.getOrElse(throw new RuntimeException(s"User with email ${request.email} wasn't found"))
      _ = ValidateUserIsNotVerified(user)

      expiresAt <- emailsHelper.sendEmailVerificationToken(user)
    } yield SendEmailVerificationToken.Response(expiresAt = expiresAt)
  }

  private def validations(request: SendEmailVerificationToken.Request) = {
    for {
      _ <- ValidateCaptcha(reCaptchaApi, request.captcha)
      _ <- ValidateEmailIsRegistered(usersRepository, request.email)
    } yield ()
  }
}
