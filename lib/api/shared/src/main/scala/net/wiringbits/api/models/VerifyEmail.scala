package net.wiringbits.api.models

import net.wiringbits.common.models.UserToken
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

object VerifyEmail {

  case class Request(token: UserToken)

  case class Response(noData: String = "")

  implicit val verifyEmailRequestFormat: Format[Request] = Json.format[Request]
  implicit val verifyEmailResponseFormat: Format[Response] = Json.format[Response]

  implicit val verifyEmailRequestSchema: Schema[Request] = Schema
    .derived[Request]
    .name(Schema.SName("VerifyEmailRequest"))
    .description("Request to verify an email")
  implicit val verifyEmailResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("VerifyEmailResponse"))
    .description("Response after verifying an email")
}
