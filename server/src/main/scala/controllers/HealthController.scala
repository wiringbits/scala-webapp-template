package controllers

import io.swagger.annotations.*
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import javax.inject.Inject

@Api("Misc")
class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  @ApiOperation(value = "Queries the application's health")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The app is healthy")
    )
  )
  def check() = Action { (x: Request[AnyContent] ) =>
    Ok(Json.obj())
  }
}
