package net.wiringbits.api.endpoints

import net.wiringbits.api.models
import net.wiringbits.api.models.{GetCurrentUser, Login, Logout}
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.*
import sttp.tapir.json.play.*

import java.time.Instant
import java.util.UUID

object AuthEndpoints {
  private val baseEndpoint = endpoint
    .in("auth")
    .tag("Auth")
    .errorOut(errorResponseErrorOut)

  val login: Endpoint[Unit, Login.Request, models.ErrorResponse, (Login.Response, CookieValueWithMeta), Any] =
    baseEndpoint.post
      .in("login")
      .in(
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
      .out(setUserIdCookie)
      .errorOut(oneOf(HttpErrors.badRequest))
      .summary("Log into the app")
      .description("Sets a session cookie to authenticate the following requests")

  val logout: Endpoint[Unit, Option[UUID], models.ErrorResponse, (Logout.Response, CookieValueWithMeta), Any] =
    baseEndpoint.post
      .in("logout")
      .in(userIdCookie)
      .out(jsonBody[Logout.Response].description("Successful logout").example(Logout.Response()))
      .out(setUserIdCookie)
      .errorOut(oneOf(HttpErrors.badRequest))
      .summary("Logout from the app")
      .description("Clears the session cookie that's stored securely")

  val getCurrentUser: Endpoint[Unit, Option[UUID], models.ErrorResponse, GetCurrentUser.Response, Any] =
    baseEndpoint.get
      .in("me")
      .in(userIdCookie)
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

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    login,
    logout,
    getCurrentUser
  )
}
