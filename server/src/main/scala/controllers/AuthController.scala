package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.endpoints.AuthEndpoints
import net.wiringbits.api.models.*
import org.slf4j.LoggerFactory
import play.api.mvc.request.DefaultRequestFactory
import play.api.mvc.{CookieHeaderEncoding, Session}
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction,
    requestFactory: DefaultRequestFactory,
    cookieHeaderEncoding: CookieHeaderEncoding,
    playTapirBridge: PlayTapirBridge
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def login(body: Login.Request): Future[Either[ErrorResponse, (Login.Response, String)]] =
    handleRequest {
      logger.info(s"Login API: ${body.email}")
      for {
        response <- loginAction(body)
        // TODO:
        session = Session(Map("id" -> response.id.toString))
        playCookie = requestFactory.sessionBaker.encodeAsCookie(session)
        cookieEncoded = cookieHeaderEncoding.encodeSetCookieHeader(List(playCookie))
        // TODO: shorter way?
        // TODO: config the cookie
//        cookie = CookieValueWithMeta(
//          value = playCookie.value,
//          expires = None,
//          maxAge = playCookie.maxAge.map(_.toLong),
//          domain = playCookie.domain,
//          path = playCookie.path,
//          secure = playCookie.secure,
//          httpOnly = playCookie.httpOnly,
//          sameSite = playCookie.sameSite,
//          otherDirectives = Map.empty
//        )
      } yield Right(response, cookieEncoded)
    }

  private def me(userIdMaybe: Option[String]): Future[Either[ErrorResponse, GetCurrentUser.Response]] = handleRequest {
    for {
      userId <- playTapirBridge.parseSession(userIdMaybe)
      _ = logger.info(s"Get user info: $userId")
      response <- getUserAction(userId)
    } yield Right(response)
  }

  private def logout(
      cookie: Option[String]
  ): Future[Either[ErrorResponse, (Logout.Response, String)]] = handleRequest {
    for {
      _ <- playTapirBridge.parseSession(cookie)
      _ = logger.info(s"Logout")
      encoded = requestFactory.sessionBaker.discard.toCookie
      header = cookieHeaderEncoding.encodeSetCookieHeader(List(encoded))
    } yield Right(Logout.Response(), header)
  }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(
      AuthEndpoints.login.serverLogic(login),
      AuthEndpoints.getCurrentUser.serverLogic(me),
      AuthEndpoints.logout.serverLogic(logout)
    )
  }
}
