package net.wiringbits.actions

import net.wiringbits.api.models.ForgotPassword
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.repositories.models.User
import net.wiringbits.util.EmailsHelper
import net.wiringbits.validations.{ValidateCaptcha, ValidateVerifiedUser}
import org.foo.generated.customtypes.{TypoUUID, TypoUnknownCitext}
import org.foo.generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import play.api.db.Database

import java.sql.Connection
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ForgotPasswordAction @Inject() (
    captchaApi: ReCaptchaApi,
    emailsHelper: EmailsHelper,
    database: Database
)(implicit ec: ExecutionContext) {
  given c: Connection = database.getConnection()

  def apply(request: ForgotPassword.Request): Future[ForgotPassword.Response] = {
    for {
      _ <- ValidateCaptcha(captchaApi, request.captcha)
      userMaybe <- Future(
        UsersRepoImpl.select.where(_.email === TypoUnknownCitext(request.email.string)).toList.headOption
      )

      // submit the email only when the user exists, otherwise, ignore the request
      _ <- userMaybe.map(whenExists).getOrElse(Future.unit)
    } yield ForgotPassword.Response()
  }

  private def whenExists(user: UsersRow) = {
    for {
      _ <- Future { ValidateVerifiedUser(user) }
      _ <- emailsHelper.sendPasswordRecoveryEmail(user)
    } yield ()
  }
}
