package net.wiringbits.api

import net.wiringbits.api.endpoints.*
import net.wiringbits.api.models.*
import play.api.libs.json.{Json, Reads}
import sttp.client3.*
import sttp.tapir.PublicEndpoint
import sttp.tapir.client.sttp.SttpClientInterpreter

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ApiClient {
  case class Config(serverUrl: String)
}

class ApiClient(config: ApiClient.Config)(implicit
    ex: ExecutionContext,
    sttpBackend: SttpBackend[Future, _]
) {
  private def asJson[R: Reads](strBody: String) = {
    Try {
      Json.parse(strBody).as[ErrorResponse]
    } match {
      case Success(error) => throw new RuntimeException(error.error)
      case Failure(_) =>
        Try {
          Json.parse(strBody).as[R]
        } match {
          case Success(response) => response
          case Failure(error) => throw new RuntimeException(s"Unexpected response ${error.getMessage}")
        }
    }
  }

  private val ServerAPI = sttp.model.Uri
    .parse(config.serverUrl)
    .getOrElse(throw new RuntimeException("Invalid server url"))

  private val client = SttpClientInterpreter()

  /** This is necessary for non-browser clients, this way, the cookies from the last authentication response are
    * propagated to the next requests
    */
  private var lastAuthResponse = Option.empty[Response[_]]

  private def unsafeSetLoginResponse(response: Response[_]): Unit = synchronized {
    lastAuthResponse = Some(response)
  }

  private def unsafeRemoveLoginResponse(): Unit = synchronized {
    lastAuthResponse = None
  }

  private def handleRequest[I, O](endpoint: PublicEndpoint[I, ErrorResponse, O, Any], request: I): Future[O] = {
    val savedCookies = lastAuthResponse.map(_.unsafeCookies).getOrElse(Seq.empty)

    client
      .toRequestThrowDecodeFailures(endpoint, Some(ServerAPI))
      .apply(request)
      .cookies(savedCookies)
      .send(sttpBackend)
      .map(_.body)
      .map {
        case Left(error) => throw new RuntimeException(error.error)
        case Right(response) => response
      }
  }

  def createUser(request: CreateUser.Request): Future[CreateUser.Response] =
    handleRequest(UsersEndpoints.create, request)

  def verifyEmail(request: VerifyEmail.Request): Future[VerifyEmail.Response] =
    handleRequest(UsersEndpoints.verifyEmail, request)

  def forgotPassword(request: ForgotPassword.Request): Future[ForgotPassword.Response] =
    handleRequest(UsersEndpoints.forgotPassword, request)

  def resetPassword(request: ResetPassword.Request): Future[ResetPassword.Response] =
    handleRequest(UsersEndpoints.resetPassword, request)

  def currentUser: Future[GetCurrentUser.Response] =
    handleRequest(AuthEndpoints.getCurrentUser, Some(""))

  def updateUser(request: UpdateUser.Request): Future[UpdateUser.Response] =
    handleRequest(UsersEndpoints.update, (request, Some("")))

  def updatePassword(request: UpdatePassword.Request): Future[UpdatePassword.Response] =
    handleRequest(UsersEndpoints.updatePassword, (request, Some("")))

  def getUserLogs: Future[GetUserLogs.Response] =
    handleRequest(UsersEndpoints.getLogs, Some(""))

  def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogs.Response] =
    handleRequest(AdminEndpoints.getUserLogsEndpoint, ("_", userId, ""))

  def adminGetUsers: Future[AdminGetUsers.Response] =
    handleRequest(AdminEndpoints.getUsersEndpoint, ("_", ""))

  def getEnvironmentConfig: Future[GetEnvironmentConfig.Response] =
    handleRequest(EnvironmentConfigEndpoints.getEnvironmentConfig, ())

  def sendEmailVerificationToken(
      request: SendEmailVerificationToken.Request
  ): Future[SendEmailVerificationToken.Response] =
    handleRequest(UsersEndpoints.sendEmailVerificationToken, request)

  // login and logout are special cases, since they return a cookie, sttp-client can not decode them correctly, so we have
  // to do it manually
  def login(request: Login.Request): Future[Login.Response] =
    client
      .toRequestThrowDecodeFailures(AuthEndpoints.login, Some(ServerAPI))
      .apply(request)
      .response(asStringAlways)
      .send(sttpBackend)
      .map { response =>
        unsafeSetLoginResponse(response)
        response.body
      }
      .map(asJson[Login.Response])

  def logout: Future[Logout.Response] =
    client
      .toRequestThrowDecodeFailures(AuthEndpoints.logout, Some(ServerAPI))
      .apply(Some(""))
      .response(asStringAlways)
      .send(sttpBackend)
      .map { response =>
        unsafeRemoveLoginResponse()
        response.body
      }
      .map(asJson[Logout.Response])
}
