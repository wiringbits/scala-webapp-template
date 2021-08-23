package net.wiringbits.api

import net.wiringbits.api.models._
import play.api.libs.json._
import sttp.client._
import sttp.model._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ApiClient {

  def createUser(request: CreateUserRequest): Future[CreateUserResponse]
  def login(request: LoginRequest): Future[LoginResponse]

  def currentUser(jwt: String): Future[GetCurrentUserResponse]
  def updateUser(jwt: String, request: UpdateUserRequest): Future[UpdateUserResponse]
  def getUserLogs(jwt: String): Future[GetUserLogsResponse]

  def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogsResponse]
  def adminGetUsers(): Future[AdminGetUsersResponse]
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
      backend: SttpBackend[Future, Nothing, Nothing],
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

    override def createUser(request: CreateUserRequest): Future[CreateUserResponse] = {
      val path = ServerAPI.path :+ "users"
      val uri = ServerAPI.path(path)

      prepareRequest[CreateUserResponse]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def login(request: LoginRequest): Future[LoginResponse] = {
      val path = ServerAPI.path :+ "users" :+ "login"
      val uri = ServerAPI.path(path)

      prepareRequest[LoginResponse]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def currentUser(jwt: String): Future[GetCurrentUserResponse] = {
      val path = ServerAPI.path :+ "users" :+ "me"
      val uri = ServerAPI.path(path)

      prepareRequest[GetCurrentUserResponse]
        .get(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updateUser(jwt: String, request: UpdateUserRequest): Future[UpdateUserResponse] = {
      val path = ServerAPI.path :+ "users"
      val uri = ServerAPI.path(path)

      prepareRequest[UpdateUserResponse]
        .put(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .body(Json.toJson(request).toString())
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def getUserLogs(jwt: String): Future[GetUserLogsResponse] = {
      val path = ServerAPI.path :+ "users" :+ "me" :+ "logs"
      val uri = ServerAPI.path(path)

      prepareRequest[GetUserLogsResponse]
        .get(uri)
        .header("X-Authorization", s"Bearer $jwt")
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogsResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "users" :+ userId.toString :+ "logs"
      val uri = ServerAPI.path(path)

      prepareRequest[AdminGetUserLogsResponse]
        .get(uri)
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetUsers(): Future[AdminGetUsersResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "users"
      val uri = ServerAPI.path(path)

      prepareRequest[AdminGetUsersResponse]
        .get(uri)
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }
  }
}
