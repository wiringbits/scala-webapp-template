package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

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
    .in(jsonBody[Login.Request])
    .out(jsonBody[Login.Response])

  private val logout = endpoint.post
    .in("auth" / "logout")
    .in(jsonBody[Logout.Request])
    .out(jsonBody[Logout.Response])

  private val getCurrentUser = endpoint.get
    .in("auth" / "me")
    .out(jsonBody[GetCurrentUser.Response])

  val routes: List[PublicEndpoint[_, _, _, _]] = List(
    login,
    logout,
    getCurrentUser
  ).map(_.tag("Auth"))
}
