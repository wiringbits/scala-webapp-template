package net.wiringbits.api.models

import net.wiringbits.common.models.Email
import play.api.libs.json.{Format, Json}

import java.time.Instant
import net.wiringbits.common.models.Captcha

object SendEmailVerificationToken {

  case class Request(email: Email, captcha: Captcha)

  case class Response(expiresAt: Instant)

  implicit val sendEmailVerificationTokenRequestFormat: Format[Request] = Json.format[Request]
  implicit val sendEmailVerificationTokenResponseFormat: Format[Response] = Json.format[Response]

}
