package controllers

import io.swagger.annotations._
import net.wiringbits.actions._
import net.wiringbits.api.models._
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@SwaggerDefinition()
@Api("Auth")
class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  @ApiOperation(
    value = "Log into the app - API",
    notes = "Returns a JWT to authenticate following requests"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON-encoded request",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[Login.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Successful login", response = classOf[Login.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def login() = handleJsonBody[Login.Request] { request =>
    val body = request.body
    logger.info(s"Login API: ${body.email}")
    for {
      response <- loginAction(body)
    } yield Ok(Json.toJson(response)).withSession("userId" -> response.id.toString)
  }

  @ApiOperation(
    value = "Logout from the app",
    notes = "Clears the session cookie that's stored securely"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON-encoded request",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[Logout.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Successful logout", response = classOf[Logout.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def logout() = handleJsonBody[Logout.Request] { request =>
    for {
      userId <- authenticate(request)
      user <- getUserAction(userId)
    } yield {
      logger.info(s"Logout - ${user.email}")
      Ok(Json.toJson(Logout.Response())).withNewSession
    }
  }

  @ApiOperation(
    value = "Get the details for the authenticated user"
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Got user details", response = classOf[GetCurrentUser.Response]),
      new ApiResponse(code = 400, message = "Authentication failed")
    )
  )
  def getCurrentUser() = handleGET { request =>
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user info: $userId")
      response <- getUserAction(userId)
    } yield Ok(Json.toJson(response))
  }
}
