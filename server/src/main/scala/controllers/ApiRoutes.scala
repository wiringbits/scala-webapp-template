package controllers

import akka.stream.Materializer
import play.api.libs.ws.StandaloneWSClient
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.tapir.PublicEndpoint
import sttp.tapir.server.play.PlayServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiRoutes @Inject() (implicit materializer: Materializer, wsClient: StandaloneWSClient, ec: ExecutionContext)
    extends SimpleRouter {
  private val swagger = SwaggerInterpreter()
    .fromEndpoints[Future](ApiRoutes.routes, "Scala Webapp Template", "1.0")

  override def routes: Routes = PlayServerInterpreter()
    .toRoutes(swagger)
}

object ApiRoutes {
  private val routes: List[PublicEndpoint[_, _, _, _]] = List(
    HealthController.routes,
    AdminController.routes,
    AuthController.routes,
    UsersController.routes,
    EnvironmentConfigController.routes
  ).flatten
}
