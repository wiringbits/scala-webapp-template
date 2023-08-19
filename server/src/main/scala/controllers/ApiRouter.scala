package controllers

import akka.stream.Materializer
import net.wiringbits.api.endpoints.*
import net.wiringbits.config.SwaggerConfig
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.apispec.openapi.Info
import sttp.tapir.AnyEndpoint
import sttp.tapir.server.play.PlayServerInterpreter
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiRouter @Inject() (
    adminController: AdminController,
    authController: AuthController,
    healthController: HealthController,
    usersController: UsersController,
    environmentConfigController: EnvironmentConfigController,
    swaggerConfig: SwaggerConfig,
    tapirBridge: PlayTapirBridge
)(implicit materializer: Materializer, ec: ExecutionContext)
    extends SimpleRouter {
  private val swagger = SwaggerInterpreter(
    swaggerUIOptions = SwaggerUIOptions.default.copy(contextPath = List(swaggerConfig.basePath))
  )
    .fromEndpoints[Future](
      ApiRouter.routes(tapirBridge.handleSession),
      Info(
        title = swaggerConfig.info.title,
        version = swaggerConfig.info.version,
        description = Some(swaggerConfig.info.description)
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
  private def routes(removeAuth: AuthTest => String)(implicit ec: ExecutionContext): List[AnyEndpoint] = List(
    HealthEndpoints.routes,
    AdminEndpoints.routes,
    AuthEndpoints.routes(removeAuth),
    UsersEndpoints.routes,
    EnvironmentConfigEndpoints.routes
  ).flatten
}
