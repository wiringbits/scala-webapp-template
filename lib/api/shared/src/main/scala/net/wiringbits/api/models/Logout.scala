package net.wiringbits.api.models

import io.swagger.annotations._
import play.api.libs.json.{Format, Json, OFormat}

object Logout {

  @ApiModel(value = "LogoutRequest", description = "Request to log out of the app")
  case object Request
  type Request = Request.type
  @ApiModel(value = "LogoutResponse", description = "Response after logging out of the app")
  case object Response
  type Response = Response.type

  implicit val logoutRequestFormat: OFormat[Request] = RequestResponseCodec.requestResponseCodec(Request)
  implicit val logoutResponseFormat: OFormat[Response] = RequestResponseCodec.requestResponseCodec(Response)
}
