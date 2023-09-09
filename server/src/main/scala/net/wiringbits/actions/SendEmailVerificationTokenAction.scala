package net.wiringbits.actions

import net.wiringbits.api.models.SendEmailVerificationToken
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.EmailsHelper
import net.wiringbits.validations.{ValidateCaptcha, ValidateEmailIsRegistered, ValidateUserIsNotVerified}
import net.wiringbits.typo_generated.customtypes.TypoUnknownCitext
import net.wiringbits.typo_generated.public.users.UsersRepoImpl
import play.api.db.Database

import java.sql.Connection
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SendEmailVerificationTokenAction @Inject() (
    emailsHelper: EmailsHelper,
    reCaptchaApi: ReCaptchaApi,
    database: Database
)(implicit ec: ExecutionContext) {
  given c: Connection = database.getConnection()

  def apply(request: SendEmailVerificationToken.Request): Future[SendEmailVerificationToken.Response] = {
    for {
      _ <- validations(request)
      user <- Future(
        UsersRepoImpl.select
          .where(_.email === TypoUnknownCitext(request.email.string))
          .toList
          .headOption
          .getOrElse(throw new RuntimeException(s"User with email ${request.email} wasn't found"))
      )
      _ = ValidateUserIsNotVerified(user)

      expiresAt <- emailsHelper.sendEmailVerificationToken(user)
    } yield SendEmailVerificationToken.Response(expiresAt = expiresAt)
  }

  private def validations(request: SendEmailVerificationToken.Request) = {
    for {
      _ <- ValidateCaptcha(reCaptchaApi, request.captcha)
      _ <- ValidateEmailIsRegistered(request.email)
    } yield ()
  }
}
