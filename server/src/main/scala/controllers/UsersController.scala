package controllers

import io.swagger.annotations._
import net.wiringbits.actions._
import net.wiringbits.api.models._
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import net.wiringbits.config.JwtConfig
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@Api
class UsersController @Inject() (
    createUserAction: CreateUserAction,
    verifyUserEmailAction: VerifyUserEmailAction,
    loginAction: LoginAction,
    forgotPasswordAction: ForgotPasswordAction,
    resetPasswordAction: ResetPasswordAction,
    updateUserAction: UpdateUserAction,
    updatePasswordAction: UpdatePasswordAction,
    getUserAction: GetUserAction,
    getUserLogsAction: GetUserLogsAction
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
        dataTypeClass = classOf[CreateUserRequest],
        defaultValue = """{
              "name": "testname",
              "email": "help@wiringbits.net",
              "password": "somethingbetterthanthis",
              "captcha": "aabbccdd..."
            }"""
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The account was created", response = classOf[CreateUserResponse]),
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

  def verifyEmail() = handleJsonBody[VerifyEmail.Request] { request =>
    val token = request.body.token
    logger.info(s"Verify user's email: ${token.userId}")
    for {
      response <- verifyUserEmailAction(token.userId, token.token)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Log into the app",
    notes = "Returns a JWT to authenticate following requests"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON-encoded request",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[LoginRequest]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Successful login", response = classOf[LoginResponse]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
  def login() = handleJsonBody[Login.Request] { request =>
    val body = request.body
    logger.info(s"Login: ${body.email}")
    for {
      response <- loginAction(body)
    } yield Ok(Json.toJson(response))
  }

  def forgotPassword() = handleJsonBody[ForgotPassword.Request] { request =>
    val body = request.body
    logger.info(s"Send a link to reset password for user with email: ${body.email}")
    for {
      response <- forgotPasswordAction(body)
    } yield Ok(Json.toJson(response))
  }

  def resetPassword() = handleJsonBody[ResetPassword.Request] { request =>
    val body = request.body
    logger.info(s"Reset user's password: ${body.token.userId}")
    for {
      response <- resetPasswordAction(body.token.userId, body.token.token, body.password)
    } yield Ok(Json.toJson(response))
  }

  def update() = handleJsonBody[UpdateUser.Request] { request =>
    val body = request.body
    logger.info(s"Update user: $body")
    for {
      userId <- authenticate(request)
      _ <- updateUserAction(userId, body)
      response = UpdateUser.Response()
    } yield Ok(Json.toJson(response))
  }

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
    notes = "Requires a JWT"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "Authorization",
        value = "Bearer token",
        required = true,
        paramType = "header",
        dataType = "String",
        example =
          "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJleHAiOjE2NTEyNTg3MDcsImlhdCI6MTY0ODY2NjcwNywgImlkIjogIjM3NzEyOTQ2LWFlNjEtNGM4Ny1hNzEwLWQ3NjY5ZGY1OTBhOCIgfQ.oIaSw0GdIRTQF3FEA0zy-aLtF-iJTBugBEusG_HhPAv4DLjblM4yNLnwpziKg7Rc"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Got user details", response = classOf[GetCurrentUserResponse]),
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

  def getLogs() = handleGET { request =>
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user logs: $userId")
      response <- getUserLogsAction(userId)
    } yield Ok(Json.toJson(response))
  }
}

@ApiModel(value = "CreateUserRequest", description = "Request for the create user API")
case class CreateUserRequest(
    @ApiModelProperty(value = "The user's name", example = "Alex", dataType = "String")
    name: Name,
    @ApiModelProperty(value = "The user's email", example = "email@wiringbits.net", dataType = "String")
    email: Email,
    @ApiModelProperty(value = "The user's password", example = "notSoWeakPassword", dataType = "String")
    password: Password,
    @ApiModelProperty(value = "The ReCAPTCHA value", dataType = "String")
    captcha: Captcha
)

@ApiModel(value = "CreateUserResponse", description = "Response for the create user API")
case class CreateUserResponse(
    @ApiModelProperty(
      value = "The id for the created user",
      dataType = "String",
      example = "e9e8d358-b989-4dd1-834d-764cac539fb1"
    )
    id: UUID,
    @ApiModelProperty(value = "The name for the created user", dataType = "String", example = "email@wiringbits.net")
    name: Name,
    @ApiModelProperty(value = "The email for the created user", dataType = "String", example = "Alex")
    email: Email
)

case class LoginRequest(
    @ApiModelProperty(value = "The user's email", example = "alexis@wiringbits.net", dataType = "String")
    email: Email,
    @ApiModelProperty(value = "The user's password", example = "notSoWeakPassword", dataType = "String")
    password: Password,
    @ApiModelProperty(value = "The ReCAPTCHA value", dataType = "String")
    captcha: Captcha
)
case class LoginResponse(
    @ApiModelProperty(
      value = "The id for the user",
      dataType = "String",
      example = "e9e8d358-b989-4dd1-834d-764cac539fb1"
    )
    id: UUID,
    @ApiModelProperty(value = "The name for the user", dataType = "String", example = "email@wiringbits.net")
    name: Name,
    @ApiModelProperty(value = "The email for the user", dataType = "String", example = "Alex")
    email: Email,
    @ApiModelProperty(
      value = "The JWT for the user",
      dataType = "String",
      example =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJleHAiOjE2NTEyNTg3MDcsImlhdCI6MTY0ODY2NjcwNywgImlkIjogIjM3NzEyOTQ2LWFlNjEtNGM4Ny1hNzEwLWQ3NjY5ZGY1OTBhOCIgfQ.oIaSw0GdIRTQF3FEA0zy-aLtF-iJTBugBEusG_HhPAv4DLjblM4yNLnwpziKg7Rc"
    )
    token: String
)

case class GetCurrentUserResponse(
    @ApiModelProperty(
      value = "The id for the user",
      dataType = "String",
      example = "e9e8d358-b989-4dd1-834d-764cac539fb1"
    )
    id: UUID,
    @ApiModelProperty(value = "The name for the user", dataType = "String", example = "email@wiringbits.net")
    name: Name,
    @ApiModelProperty(value = "The email for the user", dataType = "String", example = "Alex")
    email: Email,
    @ApiModelProperty(
      value = "The timestamp when the user was created",
      dataType = "String",
      example = "2022-03-30T18:18:25.575123Z"
    )
    createdAt: Instant
)
