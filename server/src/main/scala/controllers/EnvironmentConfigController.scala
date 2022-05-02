package controllers

import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import net.wiringbits.actions.GetEnvironmentConfigAction
import net.wiringbits.api.models.GetEnvironmentConfig
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@Api("Misc")
class EnvironmentConfigController @Inject() (
    getEnvironmentConfigAction: GetEnvironmentConfigAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  @ApiOperation(
    value = "Get the config values for the current environment",
    notes = "These values are required by the frontend app to interact with the backend"
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "Got the config values",
        response = classOf[GetEnvironmentConfig.Response]
      )
    )
  )
  def getEnvironmentConfig() = handleGET { _ =>
    logger.info("Get frontend config")
    for {
      response <- getEnvironmentConfigAction()
    } yield Ok(Json.toJson(response))
  }
}
