package controllers

import akka.stream.Materializer
import net.wiringbits.actions.GetEnvironmentConfigAction
import net.wiringbits.api.models.GetEnvironmentConfig
import org.slf4j.LoggerFactory
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.model.MediaType
import sttp.tapir.server.play.PlayServerInterpreter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EnvironmentConfigController @Inject() (
    getEnvironmentConfigAction: GetEnvironmentConfigAction
)(implicit ec: ExecutionContext, mat: Materializer)
    extends SimpleRouter {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val interpreter = PlayServerInterpreter()

  private def getEnvironmentConfig: Future[Either[Unit, GetEnvironmentConfig.Response]] = {
    logger.info("Get frontend config")
    for {
      response <- getEnvironmentConfigAction()
    } yield Right(response)
  }

  override def routes: Routes = {
    interpreter.toRoutes(EnvironmentConfigController.getEnvironmentConfig.serverLogic(_ => getEnvironmentConfig))
  }
}

object EnvironmentConfigController {
  import sttp.model.Header
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val baseEndpoint = endpoint
    .in("environment-config")
    .tag("Misc")

  private val getEnvironmentConfig = baseEndpoint.get
    .out(
      jsonBody[GetEnvironmentConfig.Response]
        .description("Got the config values")
        .example(GetEnvironmentConfig.Response("siteKey"))
    )
    .summary("Get the config values for the current environment")
    .description("These values are required by the frontend app to interact with the backend")

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    getEnvironmentConfig
  )
}
