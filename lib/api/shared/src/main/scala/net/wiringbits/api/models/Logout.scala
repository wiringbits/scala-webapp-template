package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

object Logout {
  
  case class Request(noData: String = "")
  
  case class Response(noData: String = "")

  implicit val logoutRequestFormat: Format[Request] = Json.format[Request]
  implicit val logoutResponseFormat: Format[Response] = Json.format[Response]
}
