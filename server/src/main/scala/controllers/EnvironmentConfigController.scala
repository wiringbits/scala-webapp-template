package controllers

import net.wiringbits.actions.GetEnvironmentConfigAction
import net.wiringbits.api.models.GetEnvironmentConfig
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import sttp.model.MediaType

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class EnvironmentConfigController @Inject() (
    getEnvironmentConfigAction: GetEnvironmentConfigAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getEnvironmentConfig: Action[AnyContent] = handleGET { _ =>
    logger.info("Get frontend config")
    for {
      response <- getEnvironmentConfigAction()
    } yield Ok(Json.toJson(response))
  }
}

object EnvironmentConfigController {
  import sttp.model.Header
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val getEnvironmentConfig = endpoint.get
    .in("environment-config")
    .out(
      jsonBody[GetEnvironmentConfig.Response]
        .description("Got the config values")
        .example(GetEnvironmentConfig.Response("siteKey"))
    )
    .summary("Get the config values for the current environment")
    .description("These values are required by the frontend app to interact with the backend")

  val routes: List[PublicEndpoint[_, _, _, _]] = List(
    getEnvironmentConfig
  ).map(_.tag("Misc"))
}
