package net.wiringbits.api.endpoints

import net.wiringbits.api.models
import net.wiringbits.api.models.{ErrorResponse, GetCurrentUser, Login, Logout}
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import sttp.tapir.*
import sttp.tapir.json.play.*
import sttp.tapir.model.ServerRequest

import java.time.Instant
import java.util.UUID
import scala.concurrent.Future

object AuthEndpoints {
  private val baseEndpoint = endpoint
    .in("auth")
    .tag("Auth")
    .errorOut(errorResponseErrorOut)

  def login(
      userId: Option[UUID],
      authFun: AuthTest => String
  ): Endpoint[Unit, Login.Request, ErrorResponse, Login.Response, Any] =
    baseEndpoint.post
      .in("login")
      .in(jsonBody[Login.Request])
      .out(tests(AuthTest.SetSession(userId), authFun))
      .out(jsonBody[Login.Response])

  val testEndpoint = baseEndpoint.post
    .in("test")
    .in(
      jsonBody[Login.Request].example(
        Login.Request(
          email = Email.trusted("alexis@wiringbits.net"),
          password = Password.trusted("notSoWeakPassword"),
          captcha = Captcha.trusted("captcha")
        )
      )
    )

  val login: Endpoint[Login.Request, Unit, ErrorResponse, (Login.Response, String), Any] =
    baseEndpoint.post
      .in("login")
      .securityIn(
        jsonBody[Login.Request].example(
          Login.Request(
            email = Email.trusted("alexis@wiringbits.net"),
            password = Password.trusted("notSoWeakPassword"),
            captcha = Captcha.trusted("captcha")
          )
        )
      )
      .out(
        jsonBody[Login.Response]
          .description("Successful login")
          .example(
            Login.Response(
              id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
              name = Name.trusted("Alexis"),
              email = Email.trusted("alexis@wiringbits.net")
            )
          )
      )
      .out(setSessionHeader)
      .errorOut(oneOf(HttpErrors.badRequest))
      .summary("Log into the app")
      .description("Sets a session cookie to authenticate the following requests")

  def logout(authFun: AuthTest => String)(implicit
      authHandler: ServerRequest => Future[UUID]
  ): Endpoint[Unit, Future[UUID], ErrorResponse, Logout.Response, Any] =
    baseEndpoint.post
      .in("logout")
      .in(userAuth)
      .out(jsonBody[Logout.Response].description("Successful logout").example(Logout.Response()))
//      .out(setSessionHeader)
      .out(tests(AuthTest.RemoveSession, authFun))
      .errorOut(oneOf(HttpErrors.badRequest))
      .summary("Logout from the app")
      .description("Clears the session cookie that's stored securely")

  def getCurrentUser(implicit
      authHandler: ServerRequest => Future[UUID]
  ): Endpoint[Unit, Future[UUID], ErrorResponse, GetCurrentUser.Response, Any] =
    baseEndpoint.get
      .in("me")
      .in(userAuth)
      .out(
        jsonBody[GetCurrentUser.Response]
          .description("Got user details")
          .example(
            GetCurrentUser.Response(
              id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
              name = Name.trusted("Alexis"),
              email = Email.trusted("alexis@wiringbits.net"),
              createdAt = Instant.parse("2021-01-01T00:00:00Z")
            )
          )
      )
      .summary("Get the details for the authenticated user")

  def routes(removeAuth: AuthTest => String)(implicit authHandler: ServerRequest => Future[UUID]): List[AnyEndpoint] =
    List(
      login,
      logout(removeAuth),
      getCurrentUser
    )
}

enum AuthTest {
  case RemoveSession
  case SetSession(userId: Option[UUID])
}
