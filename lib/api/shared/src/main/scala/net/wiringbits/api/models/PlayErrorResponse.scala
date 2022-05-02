package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import play.api.libs.json.{Format, Json}

// play json errors are like:
// {"error":{"requestId":2,"message":"Invalid Json: ..."}}

@ApiModel(value = "PlayErrorResponse", description = "Response with an application error")
case class PlayErrorResponse(error: PlayErrorResponse.PlayError)

object PlayErrorResponse {
  case class PlayError(message: String)

  implicit val playErrorResponseErrorFormat: Format[PlayError] = Json.format[PlayError]
  implicit val playErrorResponseFormat: Format[PlayErrorResponse] = Json.format[PlayErrorResponse]
}
