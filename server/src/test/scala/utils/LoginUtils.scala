package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.{CreateUser, Login, VerifyEmail}
import net.wiringbits.common.models.{Captcha, Password}

import scala.concurrent.{ExecutionContext, Future}

trait LoginUtils {

  def createVerifyLoginUser(
      request: CreateUser.Request,
      client: ApiClient
  )(implicit ec: ExecutionContext): Future[Login.Response] = for {
    user <- client.createUser(request)

    _ <- client.verifyEmail(VerifyEmail.Request(user.id.toString))

    loginRequest = Login.Request(
      email = user.email,
      password = Password.trusted("test123..."),
      captcha = Captcha.trusted("test")
    )
    response <- client.login(loginRequest)
  } yield response

}
