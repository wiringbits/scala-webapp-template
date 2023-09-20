package net.wiringbits.common.models.enums

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import enumeratum.EnumEntry
import play.api.libs.json.{Format, JsString, Reads, Writes}

private[enums] trait Enum[T <: EnumEntry] extends enumeratum.Enum[T] {
  implicit val enumJobTypeColumn: Column[T] = Column.nonNull[T] { (value, _) =>
    value match {
      case string: String =>
        withNameInsensitiveOption(string) match
          case Some(value) => Right(value)
          case None => Left(TypeDoesNotMatch(s"Unknown enum: $string"))
      case _ => Left(TypeDoesNotMatch("Error parsing the enum"))
    }
  }

  implicit val enumOrdering: Ordering[T] = Ordering.by(_.entryName)

  implicit val enumToStatement: ToStatement[T] =
    ToStatement[T]((s, index, v) => s.setObject(index, v.entryName))

  implicit val enumParameterMetaData: ParameterMetaData[T] = new ParameterMetaData[T] {
    override def sqlType: String = "TEXT"

    override def jdbcType: Int = java.sql.Types.VARCHAR
  }

  implicit val enumFormat: Format[T] = Format[T](
    fjs = implicitly[Reads[String]].map(string => withNameInsensitive(string)),
    tjs = Writes[T](i => JsString(i.entryName))
  )

  implicit val enumReads: Reads[T] =
    implicitly[Reads[String]].map(string => withNameInsensitive(string))

  implicit val enumWrites: Writes[T] =
    Writes[T](i => JsString(i.entryName))
}
