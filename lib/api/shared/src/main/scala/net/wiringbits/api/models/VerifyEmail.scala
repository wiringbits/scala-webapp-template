package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

object VerifyEmail {
  case class Request(
      token: String
  )
  case class Response(
      noData: String = ""
  )

  implicit val verifyEmailRequestFormat: Format[Request] = Json.format[Request]
  implicit val verifyEmailResponseFormat: Format[Response] = Json.format[Response]
}
