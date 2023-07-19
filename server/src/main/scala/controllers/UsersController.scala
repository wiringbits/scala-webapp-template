package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UsersController @Inject() (
    createUserAction: CreateUserAction,
    verifyUserEmailAction: VerifyUserEmailAction,
    forgotPasswordAction: ForgotPasswordAction,
    resetPasswordAction: ResetPasswordAction,
    updateUserAction: UpdateUserAction,
    updatePasswordAction: UpdatePasswordAction,
    getUserLogsAction: GetUserLogsAction,
    sendEmailVerificationTokenAction: SendEmailVerificationTokenAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def create: Action[CreateUser.Request] = handleJsonBody[CreateUser.Request] { request =>
    val body = request.body
    logger.info(s"Create user: ${body.email.string}")
    for {
      response <- createUserAction(body)
    } yield Ok(Json.toJson(response))
  }

  def verifyEmail: Action[VerifyEmail.Request] = handleJsonBody[VerifyEmail.Request] { request =>
    val token = request.body.token
    logger.info(s"Verify user's email: ${token.userId}")
    for {
      response <- verifyUserEmailAction(token.userId, token.token)
    } yield Ok(Json.toJson(response))
  }

  def forgotPassword: Action[ForgotPassword.Request] = handleJsonBody[ForgotPassword.Request] { request =>
    val body = request.body
    logger.info(s"Send a link to reset password for user with email: ${body.email}")
    for {
      response <- forgotPasswordAction(body)
    } yield Ok(Json.toJson(response))
  }

  def resetPassword: Action[ResetPassword.Request] = handleJsonBody[ResetPassword.Request] { request =>
    val body = request.body
    logger.info(s"Reset user's password: ${body.token.userId}")
    for {
      response <- resetPasswordAction(body.token.userId, body.token.token, body.password)
    } yield Ok(Json.toJson(response))
  }

  def sendEmailVerificationToken: Action[SendEmailVerificationToken.Request] =
    handleJsonBody[SendEmailVerificationToken.Request] { request =>
      val body = request.body
      logger.info(s"Send email to: ${body.email}")
      for {
        response <- sendEmailVerificationTokenAction(body)
      } yield Ok(Json.toJson(response))
    }

  def update: Action[UpdateUser.Request] = handleJsonBody[UpdateUser.Request] { request =>
    val body = request.body
    logger.info(s"Update user: $body")
    for {
      userId <- authenticate(request)
      _ <- updateUserAction(userId, body)
      response = UpdateUser.Response()
    } yield Ok(Json.toJson(response))
  }

  def updatePassword: Action[UpdatePassword.Request] = handleJsonBody[UpdatePassword.Request] { request =>
    val body = request.body
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Update password for: $userId")
      _ <- updatePasswordAction(userId, body)
      response = UpdateUser.Response()
    } yield Ok(Json.toJson(response))
  }

  def getLogs: Action[AnyContent] = handleGET { request =>
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user logs: $userId")
      response <- getUserLogsAction(userId)
    } yield Ok(Json.toJson(response))
  }
}

object UsersController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val create = endpoint.post
    .in("users")
    .in(jsonBody[CreateUser.Request])
    .out(jsonBody[CreateUser.Response])

  private val verifyEmail = endpoint.post
    .in("users" / "verify-email")
    .in(jsonBody[VerifyEmail.Request])
    .out(jsonBody[VerifyEmail.Response])

  private val forgotPassword = endpoint.post
    .in("users" / "forgot-password")
    .in(jsonBody[ForgotPassword.Request])
    .out(jsonBody[ForgotPassword.Response])

  private val resetPassword = endpoint.post
    .in("users" / "reset-password")
    .in(jsonBody[ResetPassword.Request])
    .out(jsonBody[ResetPassword.Response])

  private val sendEmailVerificationToken = endpoint.post
    .in("users" / "email-verification-token")
    .in(jsonBody[SendEmailVerificationToken.Request])
    .out(jsonBody[SendEmailVerificationToken.Response])

  private val update = endpoint.put
    .in("users" / "me")
    .in(jsonBody[UpdateUser.Request])
    .out(jsonBody[UpdateUser.Response])

  private val updatePassword = endpoint.put
    .in("users" / "me" / "password")
    .in(jsonBody[UpdatePassword.Request])
    .out(jsonBody[UpdatePassword.Response])

  private val getLogs = endpoint.get
    .in("users" / "me" / "logs")
    .out(jsonBody[GetUserLogs.Response])

  val routes: List[PublicEndpoint[_, _, _, _]] = List(
    create,
    verifyEmail,
    forgotPassword,
    resetPassword,
    sendEmailVerificationToken,
    update,
    updatePassword,
    getLogs
  ).map(_.tag("Users"))
}
