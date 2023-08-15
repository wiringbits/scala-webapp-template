package net.wiringbits.api

import net.wiringbits.api.endpoints.*
import net.wiringbits.api.models.*
import sttp.client3.SttpBackend
import sttp.tapir.PublicEndpoint
import sttp.tapir.client.sttp.SttpClientInterpreter

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object SttpTapirApiClient {
  case class Config(serverUrl: String)
}

class SttpTapirApiClient(config: SttpTapirApiClient.Config)(implicit
    ex: ExecutionContext,
    sttpBackend: SttpBackend[Future, _]
) {
  private val ServerAPI = sttp.model.Uri
    .parse(config.serverUrl)
    .getOrElse(throw new RuntimeException("Invalid server url"))

  private val client = SttpClientInterpreter()

  private def handleRequest[I, O](endpoint: PublicEndpoint[I, _, O, Any], request: I): Future[O] = {
    client
      .toRequestThrowErrors(endpoint, Some(ServerAPI))
      .apply(request)
      .send(sttpBackend)
      .map(_.body)
  }

  private def handleRequest[I, O](
      endpoint: PublicEndpoint[(I, Option[String]), _, O, Any],
      request: I,
      cookie: Option[String]
  ): Future[O] = {
    // TODO: find a way to handle cookies in a better way
    handleRequest(endpoint, (request, cookie))
  }

  def createUser(request: CreateUser.Request): Future[CreateUser.Response] =
    handleRequest(UsersEndpoints.create, request)

  def verifyEmail(request: VerifyEmail.Request): Future[VerifyEmail.Response] =
    handleRequest(UsersEndpoints.verifyEmail, request)

  def forgotPassword(request: ForgotPassword.Request): Future[ForgotPassword.Response] =
    handleRequest(UsersEndpoints.forgotPassword, request)

  def resetPassword(request: ResetPassword.Request): Future[ResetPassword.Response] =
    handleRequest(UsersEndpoints.resetPassword, request)

  def currentUser(sessionCookie: Option[String]): Future[GetCurrentUser.Response] =
    handleRequest(AuthEndpoints.getCurrentUser, sessionCookie)

  def updateUser(request: UpdateUser.Request, sessionCookie: Option[String]): Future[UpdateUser.Response] =
    handleRequest(UsersEndpoints.update, request, sessionCookie)

  def updatePassword(request: UpdatePassword.Request, sessionCookie: Option[String]): Future[UpdatePassword.Response] =
    handleRequest(UsersEndpoints.updatePassword, request, sessionCookie)

  def getUserLogs(sessionCookie: Option[String]): Future[GetUserLogs.Response] =
    handleRequest(UsersEndpoints.getLogs, sessionCookie)

  def adminGetUserLogs(adminAuth: String, userId: UUID, adminHeader: String): Future[AdminGetUserLogs.Response] =
    handleRequest(AdminEndpoints.getUserLogsEndpoint, (adminAuth, userId, adminHeader))

  def adminGetUsers(adminAuth: String, adminHeader: String): Future[AdminGetUsers.Response] =
    handleRequest(AdminEndpoints.getUsersEndpoint, (adminAuth, adminHeader))

  def getEnvironmentConfig: Future[GetEnvironmentConfig.Response] =
    handleRequest(EnvironmentConfigEndpoints.getEnvironmentConfig, ())

  def sendEmailVerificationToken(
      request: SendEmailVerificationToken.Request
  ): Future[SendEmailVerificationToken.Response] =
    handleRequest(UsersEndpoints.sendEmailVerificationToken, request)
}
