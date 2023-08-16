package net.wiringbits.api

import net.wiringbits.api.models._
import play.api.libs.json._
import sttp.client3._
import sttp.model._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ApiClient {
  def login(request: Login.Request): Future[Login.Response]
  def logout(): Future[Logout.Response]
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

    /** This is necessary for non-browser clients, this way, the cookies from the last authentication response are
      * propagated to the next requests
      */
    private var lastAuthResponse = Option.empty[Response[_]]

    private def unsafeSetLoginResponse(response: Response[_]): Unit = synchronized {
      lastAuthResponse = Some(response)
    }

    private def prepareRequest[R: Reads] = {
      val base = basicRequest
        .contentType(MediaType.ApplicationJson)
        .response(asJson[R])

      lastAuthResponse
        .map(base.cookies)
        .getOrElse(base)
    }

    override def login(request: Login.Request): Future[Login.Response] = {
      val path = ServerAPI.path :+ "auth" :+ "login"
      val uri = ServerAPI.withPath(path)

      prepareRequest[Login.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map { response =>
          // non-browser clients require the auth cookie to be set manually, hence, we need to store it
          unsafeSetLoginResponse(response)
          response.body
        }
        .flatMap(Future.fromTry)
    }

    override def logout(): Future[Logout.Response] = {
      val path = ServerAPI.path :+ "auth" :+ "logout"
      val uri = ServerAPI.withPath(path)

      prepareRequest[Logout.Response]
        .post(uri)
        .body(Json.toJson(Logout.Request()).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }
  }
}
