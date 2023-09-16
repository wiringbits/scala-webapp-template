package net.wiringbits.common.models.enums

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.{Format, JsString, Reads, Writes}

sealed trait BackgroundJobStatus extends EnumEntry with Uppercase

object BackgroundJobStatus extends Enum[BackgroundJobStatus] {
  case object Success extends BackgroundJobStatus
  case object Pending extends BackgroundJobStatus
  case object Failed extends BackgroundJobStatus

  val values: IndexedSeq[BackgroundJobStatus] = findValues

  implicit val backgroundJobStatusColumn: Column[BackgroundJobStatus] = Column.nonNull[BackgroundJobStatus] {
    (value, _) =>
      value match {
        case string: String =>
          withNameInsensitiveOption(string) match
            case Some(value) => Right(value)
            case None => Left(TypeDoesNotMatch(s"Unknown background job status: $string"))
        case _ => Left(TypeDoesNotMatch("Error parsing the background job status"))
      }
  }

  implicit val backgroundJobStatusOrdering: Ordering[BackgroundJobStatus] = Ordering.by(_.entryName)

  implicit val backgroundJobStatusToStatement: ToStatement[BackgroundJobStatus] =
    ToStatement[BackgroundJobStatus]((s, index, v) => s.setObject(index, v.entryName))

  implicit val backgroundJobStatusParameterMetaData: ParameterMetaData[BackgroundJobStatus] =
    new ParameterMetaData[BackgroundJobStatus] {
      override def sqlType: String = "TEXT"

      override def jdbcType: Int = java.sql.Types.VARCHAR
    }

  implicit val backgroundJobStatusCustomFormat: Format[BackgroundJobStatus] = Format[BackgroundJobStatus](
    fjs = implicitly[Reads[String]].map(string => withNameInsensitive(string)),
    tjs = Writes[BackgroundJobStatus](i => JsString(i.entryName))
  )

  implicit val backgroundJobStatusCustomReads: Reads[BackgroundJobStatus] =
    implicitly[Reads[String]].map(string => withNameInsensitive(string))

  implicit val backgroundJobStatusCustomWrites: Writes[BackgroundJobStatus] =
    Writes[BackgroundJobStatus](i => JsString(i.entryName))
}
