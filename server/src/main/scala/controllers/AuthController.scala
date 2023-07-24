package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.endpoints.AuthEndpoints
import net.wiringbits.api.models.*
import net.wiringbits.common.models.*
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.StatusCode
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.ServerEndpoint

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def login(body: Login.Request): Future[Either[ErrorResponse, (Login.Response, CookieValueWithMeta)]] =
    handleRequest {
      logger.info(s"Login API: ${body.email}")
      for {
        response <- loginAction(body)
        // TODO: shorter way?
        // TODO: config the cookie
        cookie = CookieValueWithMeta(
          value = response.id.toString,
          expires = Some(Instant.now().plus(1L, ChronoUnit.DAYS)),
          maxAge = None,
          domain = None,
          path = None,
          secure = false,
          httpOnly = false,
          sameSite = None,
          otherDirectives = Map.empty
        )
      } yield Right(response, cookie)
    }

  private def me(userIdMaybe: Option[UUID]): Future[Either[ErrorResponse, GetCurrentUser.Response]] = handleRequest {
    for {
      userId <- authenticate(userIdMaybe)
      _ = logger.info(s"Get user info: $userId")
      response <- getUserAction(userId)
    } yield Right(response)
  }

  private def logout(
      userIdMaybe: Option[UUID]
  ): Future[Either[ErrorResponse, (Logout.Response, CookieValueWithMeta)]] = handleRequest {
    for {
      _ <- authenticate(userIdMaybe)
      _ = logger.info(s"Logout")
      cookie = CookieValueWithMeta(
        value = "",
        expires = Some(Instant.now()),
        maxAge = None,
        domain = None,
        path = None,
        secure = false,
        httpOnly = false,
        sameSite = None,
        otherDirectives = Map.empty
      )
    } yield Right(Logout.Response(), cookie)
  }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(
      AuthEndpoints.login.serverLogic(login),
      AuthEndpoints.getCurrentUser.serverLogic(me),
      AuthEndpoints.logout.serverLogic(logout)
    )
  }
}
