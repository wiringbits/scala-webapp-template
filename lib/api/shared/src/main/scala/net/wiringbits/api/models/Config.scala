package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

object Config {
  case class Response(siteKey: String)

  implicit val configResponseFormat: Format[Response] = Json.format[Response]
}
