package controllers

import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject

class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def check() = Action { _ =>
    Ok("")
  }
}
