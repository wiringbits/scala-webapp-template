package controllers

import akka.stream.Materializer
import net.wiringbits.api.endpoints.*
import play.api.libs.ws.StandaloneWSClient
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.apispec.openapi.Info
import sttp.tapir.{AnyEndpoint, Endpoint}
import sttp.tapir.server.play.PlayServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiRouter @Inject() (
    adminController: AdminController,
    authController: AuthController,
    healthController: HealthController,
    usersController: UsersController,
    environmentConfigController: EnvironmentConfigController
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
        adminController.routes,
        environmentConfigController.routes
      ).flatten
    )
}

object ApiRouter {
  private val routes: List[AnyEndpoint] = List(
    HealthEndpoints.routes,
    AdminEndpoints.routes,
    AuthEndpoints.routes,
    UsersEndpoints.routes,
    EnvironmentConfigEndpoints.routes
  ).flatten
}
