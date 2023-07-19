package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def login: Action[Login.Request] = handleJsonBody[Login.Request] { request =>
    val body = request.body
    logger.info(s"Login API: ${body.email}")
    for {
      response <- loginAction(body)
    } yield Ok(Json.toJson(response)).withSession("id" -> response.id.toString)
  }

  def logout: Action[Logout.Request] = handleJsonBody[Logout.Request] { request =>
    for {
      userId <- authenticate(request)
      user <- getUserAction(userId)
    } yield {
      logger.info(s"Logout - ${user.email}")
      Ok(Json.toJson(Logout.Response())).withNewSession
    }
  }

  def getCurrentUser: Action[AnyContent] = handleGET { request =>
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user info: $userId")
      response <- getUserAction(userId)
    } yield Ok(Json.toJson(response))
  }
}

object AuthController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val login = endpoint.post
    .in("auth" / "login")
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
      jsonBody[Login.Response].example(
        Login.Response(
          id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
          name = Name.trusted("Alexis"),
          email = Email.trusted("alexis@wiringbits.net")
        )
      )
    )
    .summary("Log into the app")
    .description("Sets a session cookie to authenticate the following requests")

  private val logout = endpoint.post
    .in("auth" / "logout")
    .in(jsonBody[Logout.Request].example(Logout.Request()))
    .out(jsonBody[Logout.Response].example(Logout.Response()))
    .summary("Logout from the app")
    .description("Clears the session cookie that's stored securely")

  private val getCurrentUser = endpoint.get
    .in("auth" / "me")
    .in(jsonBody[GetCurrentUser.Request].example(GetCurrentUser.Request()))
    .out(
      jsonBody[GetCurrentUser.Response].example(
        GetCurrentUser.Response(
          id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
          name = Name.trusted("Alexis"),
          email = Email.trusted("alexis@wiringbits.net"),
          createdAt = Instant.parse("2021-01-01T00:00:00Z")
        )
      )
    )
    .summary("Get the details for the authenticated user")

  val routes: List[PublicEndpoint[_, _, _, _]] = List(
    login,
    logout,
    getCurrentUser
  ).map(_.tag("Auth"))
}
