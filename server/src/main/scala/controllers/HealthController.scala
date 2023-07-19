package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}

import javax.inject.Inject

class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def check: Action[AnyContent] = Action { _ =>
    Ok(Json.obj())
  }
}

object HealthController {
  import sttp.tapir.*
  import sttp.tapir.json.circe.*

  private val check = endpoint.get
    .in("health")
    .out(stringBody)
    .summary("Queries the application's health")

  val routes: List[PublicEndpoint[_, _, _, _]] = List(
    check
  ).map(_.tag("Misc"))
}
