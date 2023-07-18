package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

object GetEnvironmentConfig {
  case class Response(recaptchaSiteKey: String)

  implicit val configResponseFormat: Format[Response] = Json.format[Response]
}
