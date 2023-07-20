package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

// play json errors are like:
// {"error":{"requestId":2,"message":"Invalid Json: ..."}}

case class PlayErrorResponse(error: PlayErrorResponse.PlayError)

object PlayErrorResponse {
  case class PlayError(message: String)

  implicit val playErrorResponseErrorFormat: Format[PlayError] = Json.format[PlayError]
  implicit val playErrorResponseFormat: Format[PlayErrorResponse] = Json.format[PlayErrorResponse]

  implicit val playErrorResponseErrorSchema: Schema[PlayError] =
    Schema.derived[PlayError].name(Schema.SName("PlayError"))
  implicit val playErrorResponseSchema: Schema[PlayErrorResponse] = Schema
    .derived[PlayErrorResponse]
    .name(Schema.SName("PlayErrorResponse"))
    .description("Response with an application error")
}
