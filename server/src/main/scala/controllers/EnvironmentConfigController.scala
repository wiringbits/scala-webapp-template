package controllers

import net.wiringbits.actions.GetEnvironmentConfigAction
import net.wiringbits.api.models.{ErrorResponse, GetEnvironmentConfig}
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.MediaType
import sttp.tapir.server.ServerEndpoint

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EnvironmentConfigController @Inject() (
    getEnvironmentConfigAction: GetEnvironmentConfigAction
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def getEnvironmentConfig: Future[Either[ErrorResponse, GetEnvironmentConfig.Response]] = handleRequest {
    logger.info("Get frontend config")
    for {
      response <- getEnvironmentConfigAction()
    } yield Right(response)
  }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(EnvironmentConfigController.getEnvironmentConfig.serverLogic(_ => getEnvironmentConfig))
  }
}

object EnvironmentConfigController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val baseEndpoint = endpoint
    .in("environment-config")
    .tag("Misc")
    .errorOut(errorResponseErrorOut)

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
