package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.{CreateUserRequest, LoginRequest, LoginResponse, VerifyEmailRequest}
import net.wiringbits.repositories.TokensRepository

import scala.concurrent.{ExecutionContext, Future}

trait LoginUtils {

  def createVerifyLoginUser(
      request: CreateUserRequest,
      client: ApiClient,
      tokensRepository: TokensRepository
  )(implicit ec: ExecutionContext): Future[LoginResponse] = for {
    user <- client.createUser(request)

    tokenMaybe <- tokensRepository.find(user.id).map(_.headOption)
    token = tokenMaybe.map(_.token).getOrElse(throw new RuntimeException("Could not find the token"))
    userIdToken = user.id.toString + "_" + token
    _ <- client.verifyEmail(VerifyEmailRequest(userIdToken))

    loginRequest = LoginRequest(
      email = user.email,
      password = "test123..."
    )
    response <- client.login(loginRequest)
  } yield response

}
