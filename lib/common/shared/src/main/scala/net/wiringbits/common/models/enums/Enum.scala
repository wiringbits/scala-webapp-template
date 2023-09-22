package net.wiringbits.common.models.enums

import enumeratum.EnumEntry
import play.api.libs.json.{Format, JsString, Reads, Writes}

import scala.language.implicitConversions

trait Enum[T <: EnumEntry] extends enumeratum.Enum[T] {
  override implicit def withNameInsensitiveOption(name: String): Option[T] = super.withNameInsensitiveOption(name)

  implicit val enumFormat: Format[T] = Format[T](
    fjs = implicitly[Reads[String]].map(string => withNameInsensitive(string)),
    tjs = Writes[T](i => JsString(i.entryName))
  )

  implicit val enumReads: Reads[T] =
    implicitly[Reads[String]].map(string => withNameInsensitive(string))

  implicit val enumWrites: Writes[T] =
    Writes[T](i => JsString(i.entryName))
}
