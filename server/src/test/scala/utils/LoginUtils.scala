package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.{CreateUser, Login, VerifyEmail}
import net.wiringbits.common.models.{Captcha, Password, UserToken}
import net.wiringbits.repositories.UserTokensRepository
import org.scalatest.OptionValues.convertOptionToValuable

import scala.concurrent.{ExecutionContext, Future}

trait LoginUtils {

  def createVerifyLoginUser(
      request: CreateUser.Request,
      client: ApiClient,
      userTokensRepository: UserTokensRepository
  )(implicit ec: ExecutionContext): Future[Login.Response] = for {
    user <- client.createUser(request)

    tokenMaybe <- userTokensRepository.find(user.id).map(_.headOption)
    token = tokenMaybe.map(_.token).getOrElse(throw new RuntimeException("Could not find the token"))

    _ <- client.verifyEmail(VerifyEmail.Request(UserToken.validate(token).value))

    loginRequest = Login.Request(
      email = user.email,
      password = Password.trusted("test123..."),
      captcha = Captcha.trusted("test")
    )
    response <- client.login(loginRequest)
  } yield response
}
