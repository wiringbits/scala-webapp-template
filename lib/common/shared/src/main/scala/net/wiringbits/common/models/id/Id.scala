package net.wiringbits.common.models.id

import play.api.libs.json.{Format, JsString, Reads, Writes}

import java.util.UUID
import scala.language.implicitConversions

trait Id {
  def value: UUID

  override def toString: String = value.toString
}

object Id {
  trait Companion[T <: Id] {
    def parse(id: UUID): T

    def randomUUID: T = parse(UUID.randomUUID())

    implicit def fromString(str: String): T = parse(UUID.fromString(str))

    implicit val idCustomReads: Reads[T] = implicitly[Reads[String]].map(string => fromString(string))

    implicit val idCustomWrites: Writes[T] = Writes[T](i => JsString(i.value.toString))

    implicit val idFormat: Format[T] = Format[T](
      fjs = implicitly[Reads[String]].map(string => fromString(string)),
      tjs = Writes[T](i => JsString(i.value.toString))
    )
  }
}
