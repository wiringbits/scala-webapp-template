package controllers

import io.swagger.annotations.Api
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject

@Api
class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def check() = Action { _ =>
    Ok("")
  }
}
