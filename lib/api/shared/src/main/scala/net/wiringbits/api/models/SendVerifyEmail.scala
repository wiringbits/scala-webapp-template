package net.wiringbits.api.models

import net.wiringbits.common.models.Email
import play.api.libs.json.{Format, Json}

object SendVerifyEmail {
    case class Request(email: Email)
    case class Response(message: String)

    implicit val sendVerifyEmailRequestFormat: Format[Request] = Json.format[Request]
    implicit val sendVerifyEmailResponseFormat: Format[Response] = Json.format[Response]
   
}
