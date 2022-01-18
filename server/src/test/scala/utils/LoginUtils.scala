package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.{CreateUser, Login, VerifyEmail}

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
      password = "test123..."
    )
    response <- client.login(loginRequest)
  } yield response

}
