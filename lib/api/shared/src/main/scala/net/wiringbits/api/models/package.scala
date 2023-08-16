package net.wiringbits.api

import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import net.wiringbits.webapp.common.models.WrappedString
import play.api.libs.json.*
import sttp.tapir.generic.auto.*
import sttp.tapir.{Codec, CodecFormat, Schema, SchemaType}

import java.time.Instant

package object models {

  /** For some reason, play-json doesn't provide support for Instant in the scalajs version, grabbing the jvm values
    * seems to work:
    *   - https://github.com/playframework/play-json/blob/master/play-json/jvm/src/main/scala/play/api/libs/json/EnvReads.scala
    *   - https://github.com/playframework/play-json/blob/master/play-json/jvm/src/main/scala/play/api/libs/json/EnvWrites.scala
    */
  implicit val instantFormat: Format[Instant] = Format[Instant](
    fjs = implicitly[Reads[String]].map(string => Instant.parse(string)),
    tjs = Writes[Instant](i => JsString(i.toString))
  )

  case class ErrorResponse(error: String)
  implicit val errorResponseFormat: Format[ErrorResponse] = Json.format[ErrorResponse]
  implicit val errorResponseSchema: Schema[ErrorResponse] = Schema
    .derived[ErrorResponse]
    .name(Schema.SName("ErrorResponse"))

  implicit def wrappedStringSchema[T <: WrappedString]: Schema[T] = Schema(SchemaType.SString())
}
