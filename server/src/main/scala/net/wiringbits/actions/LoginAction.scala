package net.wiringbits.actions

import io.scalaland.chimney.dsl.transformInto
import net.wiringbits.api.models.Login
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}
import net.wiringbits.validations.{ValidateCaptcha, ValidatePasswordMatches, ValidateVerifiedUser}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LoginAction @Inject() (
    captchaApi: ReCaptchaApi,
    usersRepository: UsersRepository,
    userLogsRepository: UserLogsRepository
)(implicit
    ec: ExecutionContext
) {
  // returns the token to use for authenticating requests
  def apply(request: Login.Request): Future[Login.Response] = {
    for {
      _ <- ValidateCaptcha(captchaApi, request.captcha)
      // the user is verified
      maybe <- usersRepository.find(request.email)
      _ = maybe.foreach(ValidateVerifiedUser.apply)

      // The password matches
      user = ValidatePasswordMatches(maybe, request.password)

      // A login token is created
      // TODO: use chimney after creating our own types
      _ <- userLogsRepository.create(user.userId, "Logged in successfully")
    } yield Login.Response(user.userId, user.name, user.email)
  }
}
