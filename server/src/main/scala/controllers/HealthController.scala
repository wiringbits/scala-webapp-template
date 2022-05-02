package controllers

import io.swagger.annotations._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject

@Api("Misc")
class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  @ApiOperation(value = "Queries the application's health")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The app is healthy")
    )
  )
  def check() = Action { _ =>
    Ok(Json.obj())
  }
}
