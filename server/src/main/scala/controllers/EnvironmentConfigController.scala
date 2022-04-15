package controllers

import io.swagger.annotations.Api
import net.wiringbits.actions.GetEnvironmentConfigAction
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@Api
class EnvironmentConfigController @Inject() (
    getEnvironmentConfigAction: GetEnvironmentConfigAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getEnvironmentConfig() = handleGET { _ =>
    logger.info("Get frontend config")
    for {
      response <- getEnvironmentConfigAction()
    } yield Ok(Json.toJson(response))
  }
}
