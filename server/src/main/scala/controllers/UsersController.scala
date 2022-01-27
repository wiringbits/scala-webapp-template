package controllers

import net.wiringbits.actions._
import net.wiringbits.api.models._
import net.wiringbits.config.JwtConfig
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

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
