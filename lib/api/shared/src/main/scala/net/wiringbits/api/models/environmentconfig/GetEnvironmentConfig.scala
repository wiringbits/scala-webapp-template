package net.wiringbits.api.models.environmentconfig

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object GetEnvironmentConfig {
  case class Response(recaptchaSiteKey: String)

  implicit val configResponseFormat: Format[Response] = Json.format[Response]

  implicit val configResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("GetEnvironmentConfigResponse"))
    .description("Request to fetch the environment config")
}
