package net.wiringbits.api.endpoints

import net.wiringbits.api.models.*
import net.wiringbits.common.models.*
import sttp.tapir.*
import sttp.tapir.json.play.*
import sttp.tapir.model.ServerRequest

import java.time.Instant
import java.util.UUID
import scala.concurrent.Future

object UsersEndpoints {
  private val baseEndpoint = endpoint
    .in("users")
    .tag("Users")
    .errorOut(errorResponseErrorOut)

  val create: Endpoint[Unit, CreateUser.Request, ErrorResponse, CreateUser.Response, Any] = baseEndpoint.post
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

  val verifyEmail: Endpoint[Unit, VerifyEmail.Request, ErrorResponse, VerifyEmail.Response, Any] = baseEndpoint.post
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

  val forgotPassword: Endpoint[Unit, ForgotPassword.Request, ErrorResponse, ForgotPassword.Response, Any] =
    baseEndpoint.post
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

  val resetPassword: Endpoint[Unit, ResetPassword.Request, ErrorResponse, ResetPassword.Response, Any] =
    baseEndpoint.post
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

  val sendEmailVerificationToken
      : Endpoint[Unit, SendEmailVerificationToken.Request, ErrorResponse, SendEmailVerificationToken.Response, Any] =
    baseEndpoint.post
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

  def update(implicit
      authHandler: ServerRequest => Future[UUID]
  ): Endpoint[Unit, (UpdateUser.Request, Future[UUID]), ErrorResponse, UpdateUser.Response, Any] =
    baseEndpoint.put
      .in("me")
      .in(
        jsonBody[UpdateUser.Request].example(
          UpdateUser.Request(
            name = Name.trusted("Alexis")
          )
        )
      )
      .in(userAuth)
      .out(jsonBody[UpdateUser.Response].description("The user details were updated").example(UpdateUser.Response()))
      .errorOut(oneOf(HttpErrors.badRequest))
      .summary("Updates the authenticated user details")

  def updatePassword(implicit
      authHandler: ServerRequest => Future[UUID]
  ): Endpoint[Unit, (UpdatePassword.Request, Future[UUID]), ErrorResponse, UpdatePassword.Response, Any] =
    baseEndpoint.put
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
      .in(userAuth)
      .out(jsonBody[UpdatePassword.Response])
      .errorOut(oneOf(HttpErrors.badRequest))
      .summary("Updates the authenticated user password")

  def getLogs(implicit
      authHandler: ServerRequest => Future[UUID]
  ): Endpoint[Unit, Future[UUID], ErrorResponse, GetUserLogs.Response, Any] = baseEndpoint.get
    .in("me" / "logs")
    .in(userAuth)
    .out(
      jsonBody[GetUserLogs.Response]
        .description("Got user logs")
        .example(
          GetUserLogs.Response(
            List(
              GetUserLogs.Response.UserLog(
                userLogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                message = "Message",
                createdAt = Instant.parse("2021-01-01T00:00:00Z")
              )
            )
          )
        )
    )
    .errorOut(oneOf(HttpErrors.badRequest))
    .summary("Get the logs for the authenticated user")

  def routes(implicit authHandler: ServerRequest => Future[UUID]): List[AnyEndpoint] = List(
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
