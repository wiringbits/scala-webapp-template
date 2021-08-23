import net.wiringbits.api.models.ErrorResponse
import net.wiringbits.config.JwtConfig
import net.wiringbits.util.JwtUtils
import org.slf4j.LoggerFactory
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.mvc.Results.InternalServerError
import play.api.mvc.*

import java.time.Clock
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

package object controllers {
  private implicit val clock: Clock = Clock.systemUTC
  private val logger = LoggerFactory.getLogger(this.getClass)

  def adminUser(request: Request[_]): Future[String] = {
    // nginx forwards the user while using basic-authentication, which is unknown in the local environment
    val user = request.headers.get("X-Forwarded-User").getOrElse("Unknown")
    Future.successful(user)
  }

  private def decodeAuthorizationHeader(header: String)(implicit config: JwtConfig): Try[UUID] = {
    val tokenType = "Bearer"
    val headerParts = header.split(" ")

    Option(headerParts)
      .filter(_.length == 2)
      .filter(tokenType equalsIgnoreCase _.head)
      .map(_.drop(1).head)
      .map { token =>
        JwtUtils.decodeToken(config, token)
      }
      .getOrElse(Failure(new RuntimeException("Unable to parse the authorization header as a Jwt token")))
  }

  def authenticate(request: Request[_])(implicit config: JwtConfig): Future[UUID] = {
    Future.fromTry {
      request.headers
        .get("X-Authorization") // NOTE: THis allows to use nginx basic-auth
        .orElse(request.headers.get(HeaderNames.AUTHORIZATION))
        .map(header => decodeAuthorizationHeader(header))
        .getOrElse(Failure(new RuntimeException("Authorization header not found")))
    }
  }

  def handleJsonBody[T: Reads](
      block: Request[T] => Future[Result]
  )(implicit cc: ControllerComponents, ec: ExecutionContext): Action[T] = {
    cc.actionBuilder
      .async(cc.parsers.tolerantJson[T]) { request =>
        block(request).recover(errorHandler)
      }
  }

  def handleGET(
      block: Request[AnyContent] => Future[Result]
  )(implicit cc: ControllerComponents, ec: ExecutionContext): Action[AnyContent] = {
    cc.actionBuilder
      .async { request =>
        block(request).recover(errorHandler)
      }
  }

  def renderError(msg: String): JsValue = {
    Json.toJson(ErrorResponse(msg))
  }

  def errorHandler: PartialFunction[Throwable, Result] = {
    // rendering any error this way should be enough for a while
    case NonFatal(ex) =>
      // debug level used because this includes any validation error as well as server errors
      logger.debug(s"Error response while handling a request: ${ex.getMessage}", ex)
      InternalServerError(renderError(ex.getMessage))
  }
}
