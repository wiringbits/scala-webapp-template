package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.Email
import play.api.libs.json.{Format, Json}

import java.time.Instant
import net.wiringbits.common.models.Captcha

object SendEmailVerificationToken {

  @ApiModel(
    value = "SendEmailVerificationTokenRequest",
    description = "Request to re-send the token to verify an email"
  )
  case class Request(
      @ApiModelProperty(value = "The user's email", dataType = "String") email: Email,
      @ApiModelProperty(value = "ReCAPTCHA", dataType = "String") captcha: Captcha
  )

  @ApiModel(
    value = "SendEmailVerificationTokenResponse",
    description = "Response after sending the token to verify an email"
  )
  case class Response(expiresAt: Instant)

  implicit val sendEmailVerificationTokenRequestFormat: Format[Request] = Json.format[Request]
  implicit val sendEmailVerificationTokenResponseFormat: Format[Response] = Json.format[Response]

}
