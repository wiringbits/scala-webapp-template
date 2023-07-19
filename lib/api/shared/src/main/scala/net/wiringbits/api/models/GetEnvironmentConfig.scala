package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object GetEnvironmentConfig {
  case class Response(recaptchaSiteKey: String)

  implicit val configResponseFormat: Format[Response] = Json.format[Response]

  implicit val configResponseSchema: Schema[Response] =
    Schema.derived[Response].name(Schema.SName("GetEnvironmentConfigResponse"))
}
