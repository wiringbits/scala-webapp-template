package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import net.wiringbits.common.models.*
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.StatusCode
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.ServerEndpoint

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def login(body: Login.Request): Future[Either[ErrorResponse, (Login.Response, CookieValueWithMeta)]] =
    handleRequest {
      logger.info(s"Login API: ${body.email}")
      for {
        response <- loginAction(body)
        // TODO: shorter way?
        // TODO: config the cookie
        cookie = CookieValueWithMeta(
          value = response.id.toString,
          expires = Some(Instant.now().plus(1L, ChronoUnit.DAYS)),
          maxAge = None,
          domain = None,
          path = None,
          secure = false,
          httpOnly = false,
          sameSite = None,
          otherDirectives = Map.empty
        )
      } yield Right(response, cookie)
    }

  private def me(userIdMaybe: Option[UUID]): Future[Either[ErrorResponse, GetCurrentUser.Response]] = handleRequest {
    for {
      userId <- authenticate(userIdMaybe)
      _ = logger.info(s"Get user info: $userId")
      response <- getUserAction(userId)
    } yield Right(response)
  }

  private def logout(
      userIdMaybe: Option[UUID]
  ): Future[Either[ErrorResponse, (Logout.Response, CookieValueWithMeta)]] = handleRequest {
    for {
      _ <- authenticate(userIdMaybe)
      _ = logger.info(s"Logout")
      cookie = CookieValueWithMeta(
        value = "",
        expires = Some(Instant.now()),
        maxAge = None,
        domain = None,
        path = None,
        secure = false,
        httpOnly = false,
        sameSite = None,
        otherDirectives = Map.empty
      )
    } yield Right(Logout.Response(), cookie)
  }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(
      AuthController.login.serverLogic(login),
      AuthController.getCurrentUser.serverLogic(me),
      AuthController.logout.serverLogic(logout)
    )
  }
}

object AuthController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val baseEndpoint = endpoint
    .in("auth")
    .tag("Auth")
    .errorOut(errorResponseErrorOut)

  private val login = baseEndpoint.post
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

  private val logout = baseEndpoint.post
    .in("logout")
    .in(userIdCookie)
    .out(jsonBody[Logout.Response].description("Successful logout").example(Logout.Response()))
    .out(setUserIdCookie)
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Logout from the app")
    .description("Clears the session cookie that's stored securely")

  private val getCurrentUser = baseEndpoint.get
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
