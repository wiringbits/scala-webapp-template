package controllers

import io.swagger.annotations._
import net.wiringbits.actions._
import net.wiringbits.api.models._
import net.wiringbits.config.JwtConfig
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@SwaggerDefinition(
  securityDefinition = new SecurityDefinition(
    apiKeyAuthDefinitions = Array(
      new ApiKeyAuthDefinition(
        name = "Authorization",
        key = "user_jwt",
        in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER,
        description = "The user's JWT retrieved when logging into the app"
      )
    )
  )
)
@Api("Auth")
class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction
)(implicit cc: ControllerComponents, ec: ExecutionContext, jwtConfig: JwtConfig)
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
  def loginApi() = handleJsonBody[Login.Request] { request =>
    val body = request.body
    logger.info(s"Login API: ${body.email}")
    for {
      response <- loginAction(body)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Log into the app - Browser",
    notes = "Returns a session cookie that's stored securely by the browser"
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
  def loginBrowser() = handleJsonBody[Login.Request] { request =>
    val body = request.body
    logger.info(s"Login Browser: ${body.email}")
    for {
      response <- loginAction(body)
    } yield {
      // for browsers, the jwt is not required because the userId goes into the session
      // returning the jwt could allow js to touch the token which is what we are intending to avoid
      //
      // unfortunately, we have problems with the cookie propagation, which is why we keep the token
      // while running the app locally.
      val jwt = if (jwtConfig.enforced) {
        logger.warn(
          s"Browser login successful for ${body.email}, be aware that the jwt was propagated to the browser which should not occur in production"
        )
        response.token
      } else {
        ""
      }

      Ok(Json.toJson(response.copy(token = jwt)))
        .withSession("id" -> response.id.toString)
    }
  }

  @ApiOperation(
    value = "Logout from the app - Browser",
    notes = "Clears the session cookie that's stored securely by the browser",
    authorizations = Array(new Authorization(value = "user_jwt"))
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
  def logoutBrowser() = handleJsonBody[Logout.Request] { request =>
    for {
      userId <- authenticate(request)
      user <- getUserAction(userId)
    } yield {
      logger.info(s"Logout Browser - ${user.email}")

      Ok(Json.toJson(Logout.Response())).withNewSession
    }
  }

  @ApiOperation(
    value = "Get the details for the authenticated user",
    authorizations = Array(new Authorization(value = "user_jwt"))
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
