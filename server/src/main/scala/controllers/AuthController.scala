package controllers

import akka.stream.Materializer
import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.model.StatusCode
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.play.PlayServerInterpreter

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction
)(implicit ec: ExecutionContext, mat: Materializer)
    extends SimpleRouter {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val interpreter = PlayServerInterpreter()

  private def login(body: Login.Request): Future[Either[ErrorResponse, (Login.Response, CookieValueWithMeta)]] =
    handleRequest {
      logger.info(s"Login API: ${body.email}")
      for {
        response <- loginAction(body)
        // TODO: shorter way?
        cookie = CookieValueWithMeta(
          value = response.id.toString,
          expires = None,
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
    // TODO: handle userId not found
    for {
      userId <- Future {
        userIdMaybe.getOrElse(throw new RuntimeException("Unauthorized: Invalid or missing authentication"))
      }
      _ = logger.info(s"Get user info: $userId")
      response <- getUserAction(userId)
    } yield Right(response)
  }

  private def logout(
      userIdMaybe: Option[UUID]
  ): Future[Either[ErrorResponse, (Logout.Response, CookieValueWithMeta)]] = handleRequest {
    for {
      _ <- Future {
        userIdMaybe.getOrElse(throw new RuntimeException("Unauthorized: Invalid or missing authentication"))
      }
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

  override def routes: Routes = {
    interpreter
      .toRoutes(AuthController.login.serverLogic(login))
      .orElse(interpreter.toRoutes(AuthController.getCurrentUser.serverLogic(me)))
      .orElse(interpreter.toRoutes(AuthController.logout.serverLogic(logout)))
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
