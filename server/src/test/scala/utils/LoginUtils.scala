package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.{CreateUserRequest, LoginRequest, LoginResponse, VerifyEmailRequest}

import scala.concurrent.{ExecutionContext, Future}

trait LoginUtils {

  def createVerifyLoginUser(
      request: CreateUserRequest,
      client: ApiClient
  )(implicit ec: ExecutionContext): Future[LoginResponse] = for {
    user <- client.createUser(request)

    _ <- client.verifyEmail(VerifyEmailRequest(user.id.toString))

    loginRequest = LoginRequest(
      email = user.email,
      password = "test123..."
    )
    response <- client.login(loginRequest)
  } yield response

}
