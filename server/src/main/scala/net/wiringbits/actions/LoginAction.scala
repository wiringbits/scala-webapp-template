package net.wiringbits.actions

import net.wiringbits.api.models.Login
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.config.JwtConfig
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}
import net.wiringbits.util.JwtUtils
import net.wiringbits.validations.{ValidateCaptcha, ValidatePasswordMatches, ValidateVerifiedUser}

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LoginAction @Inject() (
    jwtConfig: JwtConfig,
    captchaApi: ReCaptchaApi,
    usersRepository: UsersRepository,
    userLogsRepository: UserLogsRepository
)(implicit
    ec: ExecutionContext,
    clock: Clock
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
      token = JwtUtils.createToken(jwtConfig, user.id)(clock)
    } yield Login.Response(user.id, user.name, user.email, token)
  }
}
