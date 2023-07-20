import net.wiringbits.api.models.ErrorResponse
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.mvc.Results.*
import play.api.mvc.*

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal
import net.wiringbits.api.models.errorResponseFormat
import sttp.model.StatusCode
import sttp.tapir.{EndpointOutput, oneOfVariant, statusCode}

package object controllers {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def adminUser(request: Request[_]): Future[String] = {
    // nginx forwards the user while using basic-authentication, which is unknown in the local environment
    val user = request.headers.get("X-Forwarded-User").getOrElse("Unknown")
    Future.successful(user)
  }

  def authenticate(request: Request[_])(implicit ec: ExecutionContext): Future[UUID] = {
    def userIdFromSession: Future[UUID] = Future {
      request.session
        .get("id")
        .flatMap(str => Try(UUID.fromString(str)).toOption)
        .getOrElse(throw new RuntimeException("Invalid or missing authentication"))
    }
    userIdFromSession
      .recover { case NonFatal(_) =>
        throw new RuntimeException("Unauthorized: Invalid or missing authentication")
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

  // TODO: better name?
  object HttpErrors {
    val badRequest: EndpointOutput.OneOfVariant[Unit] = oneOfVariant(
      statusCode(StatusCode.BadRequest).description("Invalid or missing arguments")
    )

    val unauthorized: EndpointOutput.OneOfVariant[Unit] = oneOfVariant(
      statusCode(StatusCode.Unauthorized).description("Invalid or missing authentication")
    )
  }
}
