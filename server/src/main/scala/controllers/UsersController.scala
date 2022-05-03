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
@Api("Users")
class UsersController @Inject() (
    createUserAction: CreateUserAction,
    verifyUserEmailAction: VerifyUserEmailAction,
    loginAction: LoginAction,
    forgotPasswordAction: ForgotPasswordAction,
    resetPasswordAction: ResetPasswordAction,
    updateUserAction: UpdateUserAction,
    updatePasswordAction: UpdatePasswordAction,
    getUserAction: GetUserAction,
    getUserLogsAction: GetUserLogsAction,
    sendEmailVerificationTokenAction: SendEmailVerificationTokenAction
)(implicit cc: ControllerComponents, ec: ExecutionContext, jwtConfig: JwtConfig)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  @ApiOperation(
    value = "Creates a new account",
    notes = "Requires a captcha"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[CreateUser.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The account was created", response = classOf[CreateUser.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def create() = handleJsonBody[CreateUser.Request] { request =>
    val body = request.body
    logger.info(s"Create user: $body")
    for {
      response <- createUserAction(body)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Verify the user's email",
    notes =
      "When an account is created, a verification code is sent to the registered email, this operations take such code and marks the email as verified"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[VerifyEmail.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The account's email was verified",
        response = classOf[VerifyEmail.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def verifyEmail() = handleJsonBody[VerifyEmail.Request] { request =>
    val token = request.body.token
    logger.info(s"Verify user's email: ${token.userId}")
    for {
      response <- verifyUserEmailAction(token.userId, token.token)
    } yield Ok(Json.toJson(response))
  }

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
    value = "Requests an email to reset a user password",
    notes = "Requires a captcha"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[ForgotPassword.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The email to recover the password was sent",
        response = classOf[ForgotPassword.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def forgotPassword() = handleJsonBody[ForgotPassword.Request] { request =>
    val body = request.body
    logger.info(s"Send a link to reset password for user with email: ${body.email}")
    for {
      response <- forgotPasswordAction(body)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Resets a user password"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[ResetPassword.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The password was updated",
        response = classOf[ResetPassword.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def resetPassword() = handleJsonBody[ResetPassword.Request] { request =>
    val body = request.body
    logger.info(s"Reset user's password: ${body.token.userId}")
    for {
      response <- resetPasswordAction(body.token.userId, body.token.token, body.password)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Sends the email verification token",
    notes =
      "The user's email should be unconfirmed, this is intended to re-send a token in case the previous one did not arrive"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[SendEmailVerificationToken.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The email with a verification token was sent",
        response = classOf[SendEmailVerificationToken.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def sendEmailVerificationToken() = handleJsonBody[SendEmailVerificationToken.Request] { request =>
    val body = request.body
    logger.info(s"Send email to: ${body.email}")
    for {
      response <- sendEmailVerificationTokenAction(body)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Updates the authenticated user details",
    authorizations = Array(new Authorization(value = "user_jwt"))
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[UpdateUser.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The user details were updated",
        response = classOf[UpdateUser.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def update() = handleJsonBody[UpdateUser.Request] { request =>
    val body = request.body
    logger.info(s"Update user: $body")
    for {
      userId <- authenticate(request)
      _ <- updateUserAction(userId, body)
      response = UpdateUser.Response()
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Updates the authenticated user password",
    notes = "The user should know its current password",
    authorizations = Array(new Authorization(value = "user_jwt"))
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[UpdatePassword.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The user password was updated",
        response = classOf[UpdatePassword.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def updatePassword() = handleJsonBody[UpdatePassword.Request] { request =>
    val body = request.body
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Update password for: $userId")
      _ <- updatePasswordAction(userId, body)
      response = UpdateUser.Response()
    } yield Ok(Json.toJson(response))
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

  @ApiOperation(
    value = "Get the logs for the authenticated user",
    authorizations = Array(new Authorization(value = "user_jwt"))
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Got user logs", response = classOf[GetUserLogs.Response]),
      new ApiResponse(code = 400, message = "Authentication failed")
    )
  )
  def getLogs() = handleGET { request =>
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user logs: $userId")
      response <- getUserLogsAction(userId)
    } yield Ok(Json.toJson(response))
  }
}
