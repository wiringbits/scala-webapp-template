package controllers

import net.wiringbits.api.endpoints.HealthEndpoints
import sttp.capabilities.WebSockets
import sttp.capabilities.pekko.PekkoStreams
import sttp.model.headers.{Cookie, CookieValueWithMeta, CookieWithMeta}
import sttp.tapir.server.ServerEndpoint

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HealthController @Inject() (implicit ec: ExecutionContext) {
  private def check: Future[Either[Unit, Unit]] =
    Future.successful(Right(()))

  def routes: List[ServerEndpoint[PekkoStreams with WebSockets, Future]] = {
    List(HealthEndpoints.check.serverLogic(_ => check))
  }
}
