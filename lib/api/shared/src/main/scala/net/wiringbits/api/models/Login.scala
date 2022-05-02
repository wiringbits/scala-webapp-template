package net.wiringbits.api.models

import io.swagger.annotations._
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import play.api.libs.json.{Format, Json}

import java.util.UUID

object Login {

  @ApiModel(value = "LoginRequest", description = "Request to log into the app")
  case class Request(
      @ApiModelProperty(value = "The user's email", example = "alexis@wiringbits.net", dataType = "String")
      email: Email,
      @ApiModelProperty(value = "The user's password", example = "notSoWeakPassword", dataType = "String")
      password: Password,
      @ApiModelProperty(value = "The ReCAPTCHA value", dataType = "String")
      captcha: Captcha
  )
  @ApiModel(value = "LoginResponse", description = "Response after logging into the app")
  case class Response(
      @ApiModelProperty(
        value = "The id for the user",
        dataType = "String",
        example = "e9e8d358-b989-4dd1-834d-764cac539fb1"
      )
      id: UUID,
      @ApiModelProperty(value = "The name for the user", dataType = "String", example = "email@wiringbits.net")
      name: Name,
      @ApiModelProperty(value = "The email for the user", dataType = "String", example = "Alex")
      email: Email,
      @ApiModelProperty(
        value = "The JWT for the user",
        dataType = "String",
        example =
          "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJleHAiOjE2NTEyNTg3MDcsImlhdCI6MTY0ODY2NjcwNywgImlkIjogIjM3NzEyOTQ2LWFlNjEtNGM4Ny1hNzEwLWQ3NjY5ZGY1OTBhOCIgfQ.oIaSw0GdIRTQF3FEA0zy-aLtF-iJTBugBEusG_HhPAv4DLjblM4yNLnwpziKg7Rc"
      )
      token: String
  )

  implicit val loginRequestFormat: Format[Request] = Json.format[Request]
  implicit val loginResponseFormat: Format[Response] = Json.format[Response]
}
