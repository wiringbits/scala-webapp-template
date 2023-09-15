package net.wiringbits.common.models

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import play.api.libs.json.{Format, JsString, Json, Reads, Writes}

import java.util.UUID
import scala.util.{Failure, Success, Try}

// Typo doesn't support correctly java UUID, so we have to do our custom UUID
// TODO: remove and replace with java UUID when Typo support it
case class UUIDCustom(value: UUID) {
  override def toString: String = value.toString
}

object UUIDCustom {
  def randomUUID(): UUIDCustom = UUIDCustom(UUID.randomUUID())

  def fromString(string: String): UUIDCustom = UUIDCustom(UUID.fromString(string))

  implicit val uuidCustomColumn: Column[UUIDCustom] = Column.nonNull[UUIDCustom] { (value, _) =>
    value match {
      case string: String =>
        Try(UUIDCustom(UUID.fromString(string))) match
          case Failure(_) => Left(TypeDoesNotMatch("Error parsing the UUID"))
          case Success(value) => Right(value)
      case uuid: UUID => Right(UUIDCustom(uuid))
      case _ => Left(TypeDoesNotMatch("Error parsing the UUID"))
    }
  }

  implicit val instantCustomOrdering: Ordering[UUIDCustom] = Ordering.by(_.value)

  implicit val instantCustomToStatement: ToStatement[UUIDCustom] =
    ToStatement[UUIDCustom]((s, index, v) => s.setObject(index, v.value))

  implicit val nameParameterMetaData: ParameterMetaData[UUIDCustom] = new ParameterMetaData[UUIDCustom] {
    override def sqlType: String = "UUID"

    override def jdbcType: Int = java.sql.Types.OTHER
  }

  implicit val instantCustomFormat: Format[UUIDCustom] = Format[UUIDCustom](
    fjs = implicitly[Reads[String]].map(string => fromString(string)),
    tjs = Writes[UUIDCustom](i => JsString(i.value.toString))
  )

  implicit val instantCustomReads: Reads[UUIDCustom] = implicitly[Reads[String]].map(string => fromString(string))

  implicit val instantCustomWrites: Writes[UUIDCustom] = Writes[UUIDCustom](i => JsString(i.value.toString))
}
