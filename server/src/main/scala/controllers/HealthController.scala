package controllers

import akka.stream.Materializer
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.tapir.server.play.PlayServerInterpreter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HealthController @Inject() (implicit ec: ExecutionContext, mat: Materializer) extends SimpleRouter {
  private val interpreter = PlayServerInterpreter()

  private def check: Future[Either[Unit, Unit]] =
    Future.successful(Right[Unit, Unit](()))

  override def routes: Routes = {
    interpreter.toRoutes(HealthController.check.serverLogic(_ => check))
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
