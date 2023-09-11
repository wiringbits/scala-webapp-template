package net.wiringbits.api

import net.wiringbits.api.endpoints.*
import net.wiringbits.api.models.*
import net.wiringbits.typo_generated.customtypes.TypoUUID
import net.wiringbits.typo_generated.public.users.UsersId
import play.api.libs.json.{Json, Reads}
import sttp.client3.*
import sttp.tapir.PublicEndpoint
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.model.ServerRequest

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
  // While the server requires a userId, it is extracted from the Session cookie, we need a dummy value just to
  // fulfill the method signatures
  private val dummyUsersId = Future.successful {
    UsersId(TypoUUID(UUID.fromString("887a5d77-cb5d-4d9c-b4dc-539c8aae3977")))
  }

  // Similarly to the dummy userId, we need a way to derive the userId from a request, which is used only on the
  // server-side code, this function is helpful to fulfill the method signatures
  private implicit val handleDummyUsersId: ServerRequest => Future[UsersId] = _ => dummyUsersId

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
    handleRequest(AuthEndpoints.getCurrentUser, dummyUsersId)

  def updateUser(request: UpdateUser.Request): Future[UpdateUser.Response] =
    handleRequest(UsersEndpoints.update, (request, dummyUsersId))

  def updatePassword(request: UpdatePassword.Request): Future[UpdatePassword.Response] =
    handleRequest(UsersEndpoints.updatePassword, (request, dummyUsersId))

  def getUserLogs: Future[GetUserLogs.Response] =
    handleRequest(UsersEndpoints.getLogs, dummyUsersId)

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
      .apply(dummyUsersId)
      .response(asStringAlways)
      .send(sttpBackend)
      .map { response =>
        unsafeRemoveLoginResponse()
        response.body
      }
      .map(asJson[Logout.Response])
}
