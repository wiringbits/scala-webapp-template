package net.wiringbits.api.models.users

import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Captcha, Email}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

import java.time.Instant

object SendEmailVerificationToken {

  case class Request(email: Email, captcha: Captcha)

  case class Response(expiresAt: Instant)

  implicit val sendEmailVerificationTokenRequestFormat: Format[Request] = Json.format[Request]
  implicit val sendEmailVerificationTokenResponseFormat: Format[Response] = Json.format[Response]

  implicit val sendEmailVerificationTokenRequestSchema: Schema[Request] = Schema
    .derived[Request]
    .name(Schema.SName("SendEmailVerificationTokenRequest"))
    .description("Request to re-send the token to verify an email")
  implicit val sendEmailVerificationTokenResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("SendEmailVerificationTokenResponse"))
    .description("Response after sending the token to verify an email")
}
