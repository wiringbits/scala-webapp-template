package net.wiringbits.actions

import net.wiringbits.api.models.Login
import net.wiringbits.apis.ReCaptchaApi
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
      _ <- userLogsRepository.create(user.id, "Logged in successfully")
    } yield Login.Response(user.id, user.name, user.email)
  }
}
