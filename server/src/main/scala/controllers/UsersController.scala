package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.models.*
import net.wiringbits.common.models.*
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UsersController @Inject() (
    createUserAction: CreateUserAction,
    verifyUserEmailAction: VerifyUserEmailAction,
    forgotPasswordAction: ForgotPasswordAction,
    resetPasswordAction: ResetPasswordAction,
    updateUserAction: UpdateUserAction,
    updatePasswordAction: UpdatePasswordAction,
    getUserLogsAction: GetUserLogsAction,
    sendEmailVerificationTokenAction: SendEmailVerificationTokenAction
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def create(request: CreateUser.Request): Future[Either[ErrorResponse, CreateUser.Response]] = handleRequest {
    logger.info(s"Create user: ${request.email.string}")
    for {
      response <- createUserAction(request)
    } yield Right(response)
  }

  private def verifyEmail(request: VerifyEmail.Request) = handleRequest {
    val token = request.token
    logger.info(s"Verify user's email: ${token.userId}")
    for {
      response <- verifyUserEmailAction(token.userId, token.token)
    } yield Right(response)
  }

  private def forgotPassword(
      request: ForgotPassword.Request
  ): Future[Either[ErrorResponse, ForgotPassword.Response]] = handleRequest {
    logger.info(s"Send a link to reset password for user with email: ${request.email}")
    for {
      response <- forgotPasswordAction(request)
    } yield Right(response)
  }

  private def resetPassword(
      request: ResetPassword.Request
  ): Future[Either[ErrorResponse, ResetPassword.Response]] = handleRequest {
    logger.info(s"Reset user's password: ${request.token.userId}")
    for {
      response <- resetPasswordAction(request.token.userId, request.token.token, request.password)
    } yield Right(response)
  }

  private def sendEmailVerificationToken(
      request: SendEmailVerificationToken.Request
  ): Future[Either[ErrorResponse, SendEmailVerificationToken.Response]] =
    handleRequest {
      logger.info(s"Send email to: ${request.email}")
      for {
        response <- sendEmailVerificationTokenAction(request)
      } yield Right(response)
    }

  private def update(
      request: UpdateUser.Request,
      userIdMaybe: Option[UUID]
  ): Future[Either[ErrorResponse, UpdateUser.Response]] = handleRequest {
    logger.info(s"Update user: $request")
    for {
      userId <- authenticate(userIdMaybe)
      _ <- updateUserAction(userId, request)
      response = UpdateUser.Response()
    } yield Right(response)
  }

  private def updatePassword(
      request: UpdatePassword.Request,
      userIdMaybe: Option[UUID]
  ): Future[Either[ErrorResponse, UpdatePassword.Response]] = handleRequest {
    for {
      userId <- authenticate(userIdMaybe)
      _ = logger.info(s"Update password for: $userId")
      _ <- updatePasswordAction(userId, request)
      response = UpdatePassword.Response()
    } yield Right(response)
  }

  private def getLogs(userIdMaybe: Option[UUID]): Future[Either[ErrorResponse, GetUserLogs.Response]] = handleRequest {
    for {
      userId <- authenticate(userIdMaybe)
      _ = logger.info(s"Get user logs: $userId")
      response <- getUserLogsAction(userId)
    } yield Right(response)
  }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(
      UsersController.create.serverLogic(create),
      UsersController.verifyEmail.serverLogic(verifyEmail),
      UsersController.forgotPassword.serverLogic(forgotPassword),
      UsersController.resetPassword.serverLogic(resetPassword),
      UsersController.sendEmailVerificationToken.serverLogic(sendEmailVerificationToken),
      UsersController.update.serverLogic(update),
      UsersController.updatePassword.serverLogic(updatePassword),
      UsersController.getLogs.serverLogic(getLogs)
    )
  }
}

object UsersController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val baseEndpoint = endpoint
    .in("users")
    .tag("Users")
    .errorOut(errorResponseErrorOut)

  private val create = baseEndpoint.post
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
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Creates a new account")
    .description("Requires a captcha")

  private val verifyEmail = baseEndpoint.post
    .in("verify-email")
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
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Verify the user's email")
    .description(
      "When an account is created, a verification code is sent to the registered email, this operations take such code and marks the email as verified"
    )

  private val forgotPassword = baseEndpoint.post
    .in("forgot-password")
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
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Requests an email to reset a user password")

  private val resetPassword = baseEndpoint.post
    .in("reset-password")
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
    .errorOut(oneOf[Unit](HttpErrors.badRequest))
    .summary("Resets a user password")

  private val sendEmailVerificationToken = baseEndpoint.post
    .in("email-verification-token")
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
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Sends the email verification token")
    .description(
      "The user's email should be unconfirmed, this is intended to re-send a token in case the previous one did not arrive"
    )

  private val update = baseEndpoint.put
    .in("me")
    .in(
      jsonBody[UpdateUser.Request].example(
        UpdateUser.Request(
          name = Name.trusted("Alexis")
        )
      )
    )
    .in(userIdCookie)
    .out(jsonBody[UpdateUser.Response].description("The user details were updated").example(UpdateUser.Response()))
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Updates the authenticated user details")

  private val updatePassword = baseEndpoint.put
    .in("me" / "password")
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
    .in(userIdCookie)
    .out(jsonBody[UpdatePassword.Response])
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Updates the authenticated user password")

  private val getLogs = baseEndpoint.get
    .in("me" / "logs")
    .in(userIdCookie)
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
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Get the logs for the authenticated user")

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    create,
    verifyEmail,
    forgotPassword,
    resetPassword,
    sendEmailVerificationToken,
    update,
    updatePassword,
    getLogs
  )
}
