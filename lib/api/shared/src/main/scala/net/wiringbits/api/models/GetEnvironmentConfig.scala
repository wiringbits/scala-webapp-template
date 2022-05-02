package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import play.api.libs.json.{Format, Json}

object GetEnvironmentConfig {
  @ApiModel(value = "GetEnvironmentConfigResponse", description = "Request to fetch the environment config")
  case class Response(recaptchaSiteKey: String)

  implicit val configResponseFormat: Format[Response] = Json.format[Response]
}
