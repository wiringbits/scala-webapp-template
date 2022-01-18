package net.wiringbits.api.models

import net.wiringbits.common.models.Captcha
import play.api.libs.json.{Format, Json}

object CreateUser {
  case class Request(name: String, email: String, password: String, captcha: Captcha)
  case class Response(name: String, email: String, token: String)
  implicit val createUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val createUserResponseFormat: Format[Response] = Json.format[Response]
}
