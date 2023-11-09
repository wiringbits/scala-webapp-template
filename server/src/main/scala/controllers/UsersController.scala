package controllers

import net.wiringbits.actions.*
import net.wiringbits.actions.users.*
import net.wiringbits.api.endpoints.UsersEndpoints
import net.wiringbits.api.models.*
import net.wiringbits.api.models.users.*
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.pekko.PekkoStreams
import sttp.tapir.server.ServerEndpoint

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

  private def forgotPassword(request: ForgotPassword.Request): Future[Either[ErrorResponse, ForgotPassword.Response]] =
    handleRequest {
      logger.info(s"Send a link to reset password for user with email: ${request.email}")
      for {
        response <- forgotPasswordAction(request)
      } yield Right(response)
    }

  private def resetPassword(request: ResetPassword.Request): Future[Either[ErrorResponse, ResetPassword.Response]] =
    handleRequest {
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
      userIdF: Future[UUID]
  ): Future[Either[ErrorResponse, UpdateUser.Response]] = handleRequest {
    logger.info(s"Update user: $request")
    for {
      userId <- userIdF
      _ <- updateUserAction(userId, request)
      response = UpdateUser.Response()
    } yield Right(response)
  }

  private def updatePassword(
      request: UpdatePassword.Request,
      userIdF: Future[UUID]
  ): Future[Either[ErrorResponse, UpdatePassword.Response]] = handleRequest {
    for {
      userId <- userIdF
      _ = logger.info(s"Update password for: $userId")
      _ <- updatePasswordAction(userId, request)
      response = UpdatePassword.Response()
    } yield Right(response)
  }

  private def getLogs(userIdF: Future[UUID]): Future[Either[ErrorResponse, GetUserLogs.Response]] =
    handleRequest {
      for {
        userId <- userIdF
        _ = logger.info(s"Get user logs: $userId")
        response <- getUserLogsAction(userId)
      } yield Right(response)
    }

  def routes: List[ServerEndpoint[PekkoStreams with WebSockets, Future]] = {
    List(
      UsersEndpoints.create.serverLogic(create),
      UsersEndpoints.verifyEmail.serverLogic(verifyEmail),
      UsersEndpoints.forgotPassword.serverLogic(forgotPassword),
      UsersEndpoints.resetPassword.serverLogic(resetPassword),
      UsersEndpoints.sendEmailVerificationToken.serverLogic(sendEmailVerificationToken),
      UsersEndpoints.update.serverLogic(update),
      UsersEndpoints.updatePassword.serverLogic(updatePassword),
      UsersEndpoints.getLogs.serverLogic(getLogs)
    )
  }
}
