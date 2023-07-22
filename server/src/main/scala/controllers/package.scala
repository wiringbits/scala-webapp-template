import net.wiringbits.api.models.{ErrorResponse, errorResponseFormat}
import org.slf4j.LoggerFactory
import sttp.model.StatusCode
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.*
import sttp.tapir.EndpointInput.AuthType
import sttp.tapir.generic.auto.*
import sttp.tapir.json.play.*

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

package object controllers {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def authenticate(userIdMaybe: Option[UUID])(implicit ec: ExecutionContext): Future[UUID] = {
    def userIdFromSession: Future[UUID] = Future {
      userIdMaybe.getOrElse(throw new RuntimeException("Invalid or missing authentication"))
    }
    userIdFromSession
      .recover { case NonFatal(_) =>
        throw new RuntimeException("Unauthorized: Invalid or missing authentication")
      }
  }

  def handleRequest[R](
      block: Future[Right[ErrorResponse, R]]
  )(implicit ec: ExecutionContext): Future[Either[ErrorResponse, R]] = {
    block.recover(errorHandler)
  }

  def errorHandler[R]: PartialFunction[Throwable, Left[ErrorResponse, R]] = {
    // rendering any error this way should be enough for a while
    case NonFatal(ex) =>
      // debug level used because this includes any validation error as well as server errors
      logger.debug(s"Error response while handling a request: ${ex.getMessage}", ex)
      Left(ErrorResponse(ex.getMessage))
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

  val adminCookie: EndpointInput.Cookie[String] = cookie[String]("X-Forwarded-User")
    .default("Unknown")
    .schema(_.hidden(true))

  val adminAuth: EndpointInput.Auth[String, AuthType.Http] = auth
    .basic[String]()
    .securitySchemeName("Basic authorization")
    .description("Admin credentials")

  val userIdCookie: EndpointInput.Cookie[Option[UUID]] = cookie[Option[UUID]]("userId")
    .description("User session")
    .schema(_.hidden(true))

  val setUserIdCookie: EndpointIO.Header[CookieValueWithMeta] = setCookie("userId")
    .description("User session")
    .schema(_.hidden(true))

  val errorResponseErrorOut: EndpointIO.Body[String, ErrorResponse] = jsonBody[ErrorResponse]
    .description("Error response")
    .example(ErrorResponse("Unauthorized: Invalid or missing authentication"))
    .schema(_.hidden(true))
}
