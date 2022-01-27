package net.wiringbits.api

import net.wiringbits.api.models._
import play.api.libs.json._
import sttp.client3._
import sttp.model._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ApiClient {
  def createUser(request: CreateUser.Request): Future[CreateUser.Response]
  def login(request: Login.Request): Future[Login.Response]

  def verifyEmail(request: VerifyEmail.Request): Future[VerifyEmail.Response]
  def forgotPassword(request: ForgotPassword.Request): Future[ForgotPassword.Response]
  def resetPassword(request: ResetPassword.Request): Future[ResetPassword.Response]

  def currentUser(jwt: String): Future[GetCurrentUser.Response]
  def updateUser(jwt: String, request: UpdateUser.Request): Future[UpdateUser.Response]
  def updatePassword(jwt: String, request: UpdatePassword.Request): Future[UpdatePassword.Response]
  def getUserLogs(jwt: String): Future[GetUserLogs.Response]

  def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogs.Response]
  def adminGetUsers(): Future[AdminGetUsers.Response]
}

object ApiClient {
  case class Config(serverUrl: String)

  private def asJson[R: Reads] = {
    asString
      .map {
        case Right(response) =>
          // handles 2xx responses
          Success(response)
        case Left(response) =>
          // handles non 2xx responses
          Try {
            val json = Json.parse(response)
            // TODO: Unify responses to match the play error format
            json
              .asOpt[ErrorResponse]
              .orElse {
                json
                  .asOpt[PlayErrorResponse]
                  .map(model => ErrorResponse(model.error.message))
              }
              .getOrElse(throw new RuntimeException(s"Unexpected JSON response: $response"))
          } match {
            case Failure(exception) =>
              println(s"Unexpected response: ${exception.getMessage}")
              exception.printStackTrace()
              Failure(new RuntimeException(s"Unexpected response, please try again in a minute"))
            case Success(value) =>
              Failure(new RuntimeException(value.error))
          }
      }
      .map { t =>
        t.map(Json.parse).map(_.as[R])
      }
  }

  // TODO: X-Authorization header is being used to keep the nginx basic-authentication
  // once that's removed, Authorization header can be used instead.
  class DefaultImpl(config: Config)(implicit
      backend: SttpBackend[Future, _],
      ec: ExecutionContext
  ) extends ApiClient {

    private val ServerAPI = sttp.model.Uri
      .parse(config.serverUrl)
      .getOrElse(throw new RuntimeException("Invalid server url"))

    private def prepareRequest[R: Reads] = {
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .response(asJson[R])
    }

    override def createUser(request: CreateUser.Request): Future[CreateUser.Response] = {
      val path = ServerAPI.path :+ "users"
      val uri = ServerAPI.withPath(path)

      prepareRequest[CreateUser.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def verifyEmail(request: VerifyEmail.Request): Future[VerifyEmail.Response] = {
      val path = ServerAPI.path :+ "users" :+ "verify-email"
      val uri = ServerAPI.withPath(path)

      prepareRequest[VerifyEmail.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def forgotPassword(request: ForgotPassword.Request): Future[ForgotPassword.Response] = {
      val path = ServerAPI.path :+ "users" :+ "forgot-password"
      val uri = ServerAPI.withPath(path)

      prepareRequest[ForgotPassword.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def resetPassword(request: ResetPassword.Request): Future[ResetPassword.Response] = {
      val path = ServerAPI.path :+ "users" :+ "reset-password"
      val uri = ServerAPI.withPath(path)

      prepareRequest[ResetPassword.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def login(request: Login.Request): Future[Login.Response] = {
      val path = ServerAPI.path :+ "users" :+ "login"
      val uri = ServerAPI.withPath(path)

      prepareRequest[Login.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def currentUser(jwt: String): Future[GetCurrentUser.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me"
      val uri = ServerAPI.withPath(path)

      prepareRequest[GetCurrentUser.Response]
        .get(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updateUser(jwt: String, request: UpdateUser.Request): Future[UpdateUser.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me"
      val uri = ServerAPI.withPath(path)

      prepareRequest[UpdateUser.Response]
        .put(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updatePassword(jwt: String, request: UpdatePassword.Request): Future[UpdatePassword.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me" :+ "password"
      val uri = ServerAPI.withPath(path)

      prepareRequest[UpdatePassword.Response]
        .put(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def getUserLogs(jwt: String): Future[GetUserLogs.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me" :+ "logs"
      val uri = ServerAPI.withPath(path)

      prepareRequest[GetUserLogs.Response]
        .get(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogs.Response] = {
      val path = ServerAPI.path :+ "admin" :+ "users" :+ userId.toString :+ "logs"
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminGetUserLogs.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetUsers(): Future[AdminGetUsers.Response] = {
      val path = ServerAPI.path :+ "admin" :+ "users"
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminGetUsers.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

  }
}
