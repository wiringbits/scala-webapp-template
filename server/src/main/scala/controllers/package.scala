import net.wiringbits.api.models.{ErrorResponse, errorResponseFormat}
import org.slf4j.LoggerFactory
import play.api.mvc.request.DefaultRequestFactory
import play.api.mvc.{CookieHeaderEncoding, RequestHeader, Session}
import sttp.tapir.model.ServerRequest

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try
import scala.util.control.NonFatal

package object controllers {
  private val logger = LoggerFactory.getLogger(this.getClass)

  class PlayTapirBridge @Inject() (
      requestFactory: DefaultRequestFactory,
      cookieHeaderEncoding: CookieHeaderEncoding
  )(implicit ec: ExecutionContext) {
    def setSession(userId: UUID): Future[String] = Future {
      val session = Session(Map("id" -> userId.toString))
      val playCookie = requestFactory.sessionBaker.encodeAsCookie(session)
      cookieHeaderEncoding.encodeSetCookieHeader(List(playCookie))
    }

    def clearSession(): Future[String] = Future {
      val encoded = requestFactory.sessionBaker.discard.toCookie
      cookieHeaderEncoding.encodeSetCookieHeader(List(encoded))
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

  // This is the way to access the play request from tapir, we need it to extract the play session
  // UUID has to be future, because we want to handle the exception in the controllers
  implicit def authHandler(serverRequest: ServerRequest)(implicit ec: ExecutionContext): Future[UUID] =
    val session = serverRequest.underlying
      .asInstanceOf[RequestHeader]
      .session

    def userIdFromSession = Future {
      session
        .get("id")
        .flatMap(str => Try(UUID.fromString(str)).toOption)
        .getOrElse(throw new RuntimeException("Invalid or missing authentication"))
    }

    userIdFromSession
      .recover { case NonFatal(_) =>
        throw new RuntimeException("Unauthorized: Invalid or missing authentication")
      }
}
