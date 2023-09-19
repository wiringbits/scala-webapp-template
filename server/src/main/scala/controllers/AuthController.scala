package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.endpoints.AuthEndpoints
import net.wiringbits.api.models.*
import net.wiringbits.common.models.id.UserId
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthController @Inject() (
    loginAction: LoginAction,
    getUserAction: GetUserAction,
    playTapirBridge: PlayTapirBridge
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def login(body: Login.Request): Future[Either[ErrorResponse, (Login.Response, String)]] =
    handleRequest {
      logger.info(s"Login API: ${body.email}")
      for {
        response <- loginAction(body)
        cookieEncoded <- playTapirBridge.setSession(response.id)
      } yield Right(response, cookieEncoded)
    }

  private def me(userIdF: Future[UserId]): Future[Either[ErrorResponse, GetCurrentUser.Response]] =
    handleRequest {
      for {
        userId <- userIdF
        _ = logger.info(s"Get user info: $userId")
        response <- getUserAction(userId)
      } yield Right(response)
    }

  private def logout(userIdF: Future[UserId]): Future[Either[ErrorResponse, (Logout.Response, String)]] =
    handleRequest {
      for {
        _ <- userIdF
        _ = logger.info("Logout")
        header <- playTapirBridge.clearSession()
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
