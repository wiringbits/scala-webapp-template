package controllers

import net.wiringbits.actions.ConfigAction
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class EnvironmentConfigController @Inject() (
    configAction: ConfigAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getEnvironmentConfig() = handleGET { _ =>
    logger.info("get frontend config")
    for {
      response <- configAction()
      _ = logger.info(response.toString)
    } yield Ok(Json.toJson(response))
  }
}
