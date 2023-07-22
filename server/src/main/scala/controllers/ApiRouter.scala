package controllers

import akka.stream.Materializer
import play.api.libs.ws.StandaloneWSClient
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.apispec.openapi.Info
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.play.PlayServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiRouter @Inject()(
    adminController: AdminController,
    authController: AuthController,
    healthController: HealthController,
    usersController: UsersController
)(implicit materializer: Materializer, wsClient: StandaloneWSClient, ec: ExecutionContext)
    extends SimpleRouter {
  private val swagger = SwaggerInterpreter()
    .fromEndpoints[Future](
      ApiRouter.routes,
      Info(
        title = "Scala webapp template's API",
        version = "beta",
        description = Some("The API for the Scala webapp template app")
      )
    )

  override def routes: Routes = PlayServerInterpreter()
    .toRoutes(
      List(
        swagger,
        usersController.routes,
        authController.routes,
        healthController.routes,
        adminController.routes
      ).flatten
    )
}

object ApiRouter {
  private val routes: List[Endpoint[_, _, _, _, _]] = List(
    HealthController.routes,
    AdminController.routes,
    AuthController.routes,
    UsersController.routes,
    EnvironmentConfigController.routes
  ).flatten
}
