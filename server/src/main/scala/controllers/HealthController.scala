package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}

import javax.inject.Inject

class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def check: Action[AnyContent] = Action { _ =>
    Ok(Json.obj())
  }
}
