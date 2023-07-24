package controllers

import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.headers.{Cookie, CookieValueWithMeta, CookieWithMeta}
import sttp.tapir.server.ServerEndpoint

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HealthController @Inject() (implicit ec: ExecutionContext) {
  private def check: Future[Either[Unit, Unit]] =
    Future.successful(Right(()))

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(HealthController.check.serverLogic(_ => check))
  }
}

object HealthController {
  import sttp.tapir.*
  import sttp.tapir.json.circe.*

  private val baseEndpoint = endpoint
    .tag("Misc")
    .in("health")

  private val check = baseEndpoint.get
    .out(emptyOutput.description("The app is healthy"))
    .summary("Queries the application's health")

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    check
  )
}
