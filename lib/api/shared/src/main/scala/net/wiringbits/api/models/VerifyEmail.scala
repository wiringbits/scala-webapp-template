package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import net.wiringbits.common.models.UserToken
import play.api.libs.json.{Format, Json, OFormat}

object VerifyEmail {

  @ApiModel(value = "VerifyEmailRequest", description = "Request to verify an email")
  case class Request(token: UserToken)

  @ApiModel(value = "VerifyEmailResponse", description = "Response after verifying an email")
  case object Response
  type Response = Response.type
  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
  implicit val verifyEmailRequestFormat: Format[Request] = Json.format[Request]

  implicit val verifyEmailResponseFormat: OFormat[Response] = RequestResponseCodec.requestResponseCodec(Response)
}
