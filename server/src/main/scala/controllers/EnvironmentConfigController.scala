package controllers

import net.wiringbits.actions.GetEnvironmentConfigAction
import net.wiringbits.api.endpoints.EnvironmentConfigEndpoints
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
    List(EnvironmentConfigEndpoints.getEnvironmentConfig.serverLogic(_ => getEnvironmentConfig))
  }
}
