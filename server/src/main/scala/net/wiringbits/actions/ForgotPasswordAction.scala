package net.wiringbits.actions

import net.wiringbits.api.models.ForgotPassword
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.repositories.models.User
import net.wiringbits.util.EmailsHelper
import net.wiringbits.validations.{ValidateCaptcha, ValidateVerifiedUser}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ForgotPasswordAction @Inject() (
    captchaApi: ReCaptchaApi,
    usersRepository: UsersRepository,
    emailsHelper: EmailsHelper
)(implicit ec: ExecutionContext) {
  def apply(request: ForgotPassword.Request): Future[ForgotPassword.Response] = {
    for {
      _ <- ValidateCaptcha(captchaApi, request.captcha)
      userMaybe <- usersRepository.find(request.email)

      // submit the email only when the user exists, otherwise, ignore the request
      _ <- userMaybe.map(whenExists).getOrElse(Future.unit)
    } yield ForgotPassword.Response()
  }

  private def whenExists(user: User) = {
    for {
      _ <- Future { ValidateVerifiedUser(user) }
      _ <- emailsHelper.sendPasswordRecoveryEmail(user)
    } yield ()
  }
}
