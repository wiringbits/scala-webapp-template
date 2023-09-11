package controllers

import net.wiringbits.actions.*
import net.wiringbits.api.endpoints.AuthEndpoints
import net.wiringbits.api.models.*
import net.wiringbits.typo_generated.customtypes.TypoUUID
import net.wiringbits.typo_generated.public.users.UsersId
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import java.util.UUID
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
        cookieEncoded <- playTapirBridge.setSession(UsersId(TypoUUID(response.id)))
      } yield Right(response, cookieEncoded)
    }

  private def me(usersIdF: Future[UsersId]): Future[Either[ErrorResponse, GetCurrentUser.Response]] =
    handleRequest {
      for {
        userId <- usersIdF
        _ = logger.info(s"Get user info: $userId")
        response <- getUserAction(userId)
      } yield Right(response)
    }

  private def logout(usersIdF: Future[UsersId]): Future[Either[ErrorResponse, (Logout.Response, String)]] =
    handleRequest {
      for {
        _ <- usersIdF
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
