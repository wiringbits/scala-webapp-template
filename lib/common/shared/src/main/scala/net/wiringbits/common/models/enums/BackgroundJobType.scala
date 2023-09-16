package net.wiringbits.common.models.enums

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.{Format, JsString, Reads, Writes}

sealed trait BackgroundJobType extends EnumEntry with Uppercase

/** NOTE: Updating this model can cause tasks to fail, for example, if SendEmail is removed while there are pending
  * SendEmail tasks stored at the database
  */
object BackgroundJobType extends Enum[BackgroundJobType] {
  case object SendEmail extends BackgroundJobType

  val values: IndexedSeq[BackgroundJobType] = findValues

  implicit val backgroundJobTypeColumn: Column[BackgroundJobType] = Column.nonNull[BackgroundJobType] { (value, _) =>
    value match {
      case string: String =>
        withNameInsensitiveOption(string) match
          case Some(value) => Right(value)
          case None => Left(TypeDoesNotMatch(s"Unknown background job type: $string"))
      case _ => Left(TypeDoesNotMatch("Error parsing the background job type"))
    }
  }

  implicit val backgroundJobTypeOrdering: Ordering[BackgroundJobType] = Ordering.by(_.entryName)

  implicit val backgroundJobTypeToStatement: ToStatement[BackgroundJobType] =
    ToStatement[BackgroundJobType]((s, index, v) => s.setObject(index, v.entryName))

  implicit val backgroundJobTypeParameterMetaData: ParameterMetaData[BackgroundJobType] =
    new ParameterMetaData[BackgroundJobType] {
      override def sqlType: String = "TEXT"

      override def jdbcType: Int = java.sql.Types.VARCHAR
    }

  implicit val backgroundJobTypeCustomFormat: Format[BackgroundJobType] = Format[BackgroundJobType](
    fjs = implicitly[Reads[String]].map(string => withNameInsensitive(string)),
    tjs = Writes[BackgroundJobType](i => JsString(i.entryName))
  )

  implicit val backgroundJobTypeCustomReads: Reads[BackgroundJobType] =
    implicitly[Reads[String]].map(string => withNameInsensitive(string))

  implicit val backgroundJobTypeCustomWrites: Writes[BackgroundJobType] =
    Writes[BackgroundJobType](i => JsString(i.entryName))
}
