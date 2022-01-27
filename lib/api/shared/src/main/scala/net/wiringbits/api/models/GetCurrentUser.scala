package net.wiringbits.api.models

import net.wiringbits.common.models.{Email, Name}
import play.api.libs.json.{Format, Json}

import java.time.Instant
import java.util.UUID

object GetCurrentUser {
  case class Request(noData: String = "")
  case class Response(id: UUID, name: Name, email: Email, createdAt: Instant)
  implicit val getUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val getUserResponseFormat: Format[Response] = Json.format[Response]
}
