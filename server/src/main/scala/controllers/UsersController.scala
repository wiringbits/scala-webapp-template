package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.endpoints.UsersEndpoints
import net.wiringbits.api.models.*
import net.wiringbits.typo_generated.customtypes.TypoUUID
import net.wiringbits.typo_generated.public.users.UsersId
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
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
      response <- verifyUserEmailAction(UsersId(TypoUUID(token.userId)), token.token)
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
        response <- resetPasswordAction(UsersId(TypoUUID(request.token.userId)), request.token.token, request.password)
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
      usersIdF: Future[UsersId]
  ): Future[Either[ErrorResponse, UpdateUser.Response]] = handleRequest {
    logger.info(s"Update user: $request")
    for {
      usersId <- usersIdF
      _ <- updateUserAction(usersId, request)
      response = UpdateUser.Response()
    } yield Right(response)
  }

  private def updatePassword(
      request: UpdatePassword.Request,
      usersIdF: Future[UsersId]
  ): Future[Either[ErrorResponse, UpdatePassword.Response]] = handleRequest {
    for {
      usersId <- usersIdF
      _ = logger.info(s"Update password for: $usersId")
      _ <- updatePasswordAction(usersId, request)
      response = UpdatePassword.Response()
    } yield Right(response)
  }

  private def getLogs(usersIdF: Future[UsersId]): Future[Either[ErrorResponse, GetUserLogs.Response]] =
    handleRequest {
      for {
        usersId <- usersIdF
        _ = logger.info(s"Get user logs: $usersId")
        response <- getUserLogsAction(usersId)
      } yield Right(response)
    }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
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
