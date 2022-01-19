package net.wiringbits.api.models

import net.wiringbits.common.models.Captcha
import net.wiringbits.common.models.{Email, Name, Password}
import play.api.libs.json.{Format, Json}

object CreateUser {
  case class Request(name: Name, email: Email, password: Password, captcha: Captcha)
  case class Response(name: Name, email: Email, token: String)

  implicit val createUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val createUserResponseFormat: Format[Response] = Json.format[Response]
}
