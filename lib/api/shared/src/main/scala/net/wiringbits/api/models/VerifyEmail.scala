package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import net.wiringbits.common.models.UserToken
import play.api.libs.json.{Format, Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}

object VerifyEmail {

  @ApiModel(value = "VerifyEmailRequest", description = "Request to verify an email")
  case class Request(token: UserToken)

  @ApiModel(value = "VerifyEmailResponse", description = "Response after verifying an email")
  case class  Response()

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
  implicit val verifyEmailRequestFormat: Format[Request] = Json.format[Request]
  
  //implicit val verifyEmailResponseFormat: Format[Response] = Json.format[Response]

  implicit val verifyEmailResponseFormat = OFormat[Response](Reads[Response] {
      case JsObject(_) => JsSuccess(Response())
      case _           => JsError("Empty object expected")
    }, OWrites[Response] { _ =>
      Json.obj()
    })
}
