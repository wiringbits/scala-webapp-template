package controllers

import net.wiringbits.actions.ConfigAction
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ConfigController @Inject() (
    configAction: ConfigAction
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getConfig() = handleGET { _ =>
    logger.info("get configs")
    for {
      response <- configAction.apply()
    } yield Ok(Json.toJson(response))
  }
}
