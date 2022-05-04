package net.wiringbits.api.models

import io.swagger.annotations._
import play.api.libs.json.{Format, Json}

object Logout {

  @ApiModel(value = "LogoutRequest", description = "Request to log out of the app")
  case class Request(noData: String = "")

  @ApiModel(value = "LogoutResponse", description = "Response after logging out of the app")
  case class Response(noData: String = "")

  implicit val logoutRequestFormat: Format[Request] = Json.format[Request]
  implicit val logoutResponseFormat: Format[Response] = Json.format[Response]
}
