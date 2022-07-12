package net.wiringbits.api.models

import io.swagger.annotations._
import play.api.libs.json.{Format, Json,OFormat}

object Logout {

  @ApiModel(value = "LogoutRequest", description = "Request to log out of the app")
  case class  Request()

  @ApiModel(value = "LogoutResponse", description = "Response after logging out of the app")
  case class  Response()

 implicit val logoutRequestFormat: OFormat[Request.type] = RequestResponseCodec.requestResponseCodec(Request)
 implicit val logoutResponseFormat: OFormat[Response.type] = RequestResponseCodec.requestResponseCodec(Response)
}
