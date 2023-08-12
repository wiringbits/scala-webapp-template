package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import net.wiringbits.common.models.*
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.time.Instant
import java.util.UUID
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
  import sttp.model.StatusCode
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val create = endpoint.post
    .in("users")
    .in(
      jsonBody[CreateUser.Request].example(
        CreateUser.Request(
          name = Name.trusted("Alexis"),
          email = Email.trusted("alexis@wiringbits.net"),
          password = Password.trusted("notSoWeakPassword"),
          captcha = Captcha.trusted("captcha")
        )
      )
    )
    .out(
      jsonBody[CreateUser.Response]
        .description("The account was created")
        .example(
          CreateUser.Response(
            id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
            name = Name.trusted("Alexis"),
            email = Email.trusted("alexis@wiringbits.net")
          )
        )
    )
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Creates a new account")
    .description("Requires a captcha")

  private val verifyEmail = endpoint.post
    .in("users" / "verify-email")
    .in(
      jsonBody[VerifyEmail.Request].example(
        VerifyEmail.Request(
          UserToken(
            userId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
            token = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")
          )
        )
      )
    )
    .out(jsonBody[VerifyEmail.Response].description("The account's email was verified").example(VerifyEmail.Response()))
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Verify the user's email")
    .description(
      "When an account is created, a verification code is sent to the registered email, this operations take such code and marks the email as verified"
    )

  private val forgotPassword = endpoint.post
    .in("users" / "forgot-password")
    .in(
      jsonBody[ForgotPassword.Request].example(
        ForgotPassword.Request(
          email = Email.trusted("alexis@wirngbits.net"),
          captcha = Captcha.trusted("captcha")
        )
      )
    )
    .out(
      jsonBody[ForgotPassword.Response]
        .description("The email to recover the password was sent")
        .example(ForgotPassword.Response())
    )
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Requests an email to reset a user password")

  private val resetPassword = endpoint.post
    .in("users" / "reset-password")
    .in(
      jsonBody[ResetPassword.Request]
        .example(
          ResetPassword.Request(
            token = UserToken(
              userId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
              token = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")
            ),
            password = Password.trusted("notSoWeakPassword")
          )
        )
    )
    .out(
      jsonBody[ResetPassword.Response]
        .description("The password was updated")
        .example(
          ResetPassword.Response(
            name = Name.trusted("Alexis"),
            email = Email.trusted("alexis@wiringbits.net")
          )
        )
    )
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Resets a user password")

  private val sendEmailVerificationToken = endpoint.post
    .in("users" / "email-verification-token")
    .in(
      jsonBody[SendEmailVerificationToken.Request].example(
        SendEmailVerificationToken.Request(
          email = Email.trusted("alexis@wiringbits.net"),
          captcha = Captcha.trusted("captcha")
        )
      )
    )
    .out(
      jsonBody[SendEmailVerificationToken.Response]
        .description("The account's email was verified")
        .example(
          SendEmailVerificationToken.Response(
            expiresAt = Instant.parse("2021-01-01T00:00:00Z")
          )
        )
    )
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Sends the email verification token")
    .description(
      "The user's email should be unconfirmed, this is intended to re-send a token in case the previous one did not arrive"
    )

  private val update = endpoint.put
    .in("users" / "me")
    .in(
      jsonBody[UpdateUser.Request].example(
        UpdateUser.Request(
          name = Name.trusted("Alexis")
        )
      )
    )
    .out(jsonBody[UpdateUser.Response].description("The user details were updated").example(UpdateUser.Response()))
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Updates the authenticated user details")

  private val updatePassword = endpoint.put
    .in("users" / "me" / "password")
    .in(
      jsonBody[UpdatePassword.Request]
        .description("The user password was updated")
        .example(
          UpdatePassword.Request(
            oldPassword = Password.trusted("oldWeakPassword"),
            newPassword = Password.trusted("newNotSoWeakPassword")
          )
        )
    )
    .out(jsonBody[UpdatePassword.Response])
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Updates the authenticated user password")

  private val getLogs = endpoint.get
    .in("users" / "me" / "logs")
    .out(
      jsonBody[GetUserLogs.Response]
        .description("Got user logs")
        .example(
          GetUserLogs.Response(
            List(
              GetUserLogs.Response.UserLog(
                id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                message = "Message",
                createdAt = Instant.parse("2021-01-01T00:00:00Z")
              )
            )
          )
        )
    )
    .errorOut(
      oneOf[Unit](
        oneOfVariant(statusCode(StatusCode.BadRequest).description("Invalid or missing arguments"))
      )
    )
    .summary("Get the logs for the authenticated user")

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
