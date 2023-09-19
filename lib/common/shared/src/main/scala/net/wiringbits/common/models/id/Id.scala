package net.wiringbits.common.models.id

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import play.api.libs.json.{Format, JsString, Reads, Writes}

import java.util.UUID
import scala.util.{Failure, Success, Try}

private[id] trait Id {
  def value: UUID
}

private[id] object Id {
  trait Companion[T <: Id] {
    def parse(id: UUID): T

    def randomUUID: T = parse(UUID.randomUUID())

    def fromString(str: String): T = parse(UUID.fromString(str))

    implicit val idToStatement: ToStatement[T] = ToStatement[T]((s, index, v) => s.setObject(index, v.value))

    implicit val idColumn: Column[T] = Column.nonNull { (value, _) =>
      value match {
        case string: String =>
          Try(parse(UUID.fromString(string))) match
            case Failure(_) => Left(TypeDoesNotMatch("Error parsing the UUID"))
            case Success(value) => Right(value)
        case uuid: UUID => Right(parse(uuid))
        case _ => Left(TypeDoesNotMatch("Error parsing the UUID"))
      }
    }

    implicit val emailOrdering: Ordering[T] = Ordering.by(_.value)

    implicit val uuidParameterMetaData: ParameterMetaData[T] = new ParameterMetaData[T] {
      override def sqlType: String = "UUID"

      override def jdbcType: Int = java.sql.Types.OTHER
    }

    implicit val idCustomReads: Reads[T] = implicitly[Reads[String]].map(string => fromString(string))

    implicit val idCustomWrites: Writes[T] = Writes[T](i => JsString(i.value.toString))

    implicit val backgroundJobStatusCustomFormat: Format[T] = Format[T](
      fjs = implicitly[Reads[String]].map(string => fromString(string)),
      tjs = Writes[T](i => JsString(i.value.toString))
    )
  }
}
