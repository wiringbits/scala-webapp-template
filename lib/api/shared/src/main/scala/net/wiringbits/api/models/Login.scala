package net.wiringbits.api.models

import net.wiringbits.common.models.Captcha
import net.wiringbits.common.models.{Email, Name, Password}

import play.api.libs.json.{Format, Json}

import java.util.UUID

object Login {
  case class Request(email: Email, password: Password, captcha: Captcha)
  case class Response(id: UUID, name: Name, email: Email, token: String)

  implicit val loginRequestFormat: Format[Request] = Json.format[Request]
  implicit val loginResponseFormat: Format[Response] = Json.format[Response]
}
