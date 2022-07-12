package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.{Captcha, Email}
import play.api.libs.json.{Format, Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}

object ForgotPassword {
  @ApiModel(value = "ForgotPasswordRequest", description = "Request to reset a forgotten password")
  case class Request(
      @ApiModelProperty(value = "The user's email", dataType = "String") email: Email,
      @ApiModelProperty(value = "ReCAPTCHA", dataType = "String") captcha: Captcha
  )

  @ApiModel(value = "ForgotPasswordResponse", description = "Response to the ForgotPasswordRequest")
  case class Response()

  implicit val forgotPasswordRequestFormat: Format[Request] = Json.format[Request]
  //implicit val forgotPasswordResponseFormat: Format[Response] = Json.format[Response] 
  implicit val forgotPasswordResponseFormat = OFormat[Response](Reads[Response] {
               case JsObject(_) => JsSuccess(Response())
               case _           => JsError("Empty object expected")
             }, OWrites[Response] { _ =>
               Json.obj()
             })
}
