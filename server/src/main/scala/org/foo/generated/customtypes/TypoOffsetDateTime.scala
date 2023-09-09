/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package org.foo.generated.customtypes

import anorm.Column
import anorm.ParameterMetaData
import anorm.ToStatement
import anorm.TypeDoesNotMatch
import java.sql.Types
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import org.postgresql.jdbc.PgArray
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import typo.dsl.Bijection

/** This is `java.time.OffsetDateTime`, but with microsecond precision and transferred to and from postgres as strings. The reason is that postgres driver and db libs are broken */
case class TypoOffsetDateTime(value: OffsetDateTime)

object TypoOffsetDateTime {
  val parser: DateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true).appendPattern("X").toFormatter
  def apply(value: OffsetDateTime): TypoOffsetDateTime = new TypoOffsetDateTime(value.truncatedTo(ChronoUnit.MICROS))  
  def now = TypoOffsetDateTime(OffsetDateTime.now)
  implicit lazy val arrayColumn: Column[Array[TypoOffsetDateTime]] = Column.nonNull[Array[TypoOffsetDateTime]]((v1: Any, _) =>
    v1 match {
        case v: PgArray =>
         v.getArray match {
           case v: Array[?] =>
             Right(v.map(v => TypoOffsetDateTime(OffsetDateTime.parse(v.asInstanceOf[String], parser))))
           case other => Left(TypeDoesNotMatch(s"Expected one-dimensional array from JDBC to produce an array of TypoOffsetDateTime, got ${other.getClass.getName}"))
         }
      case other => Left(TypeDoesNotMatch(s"Expected instance of org.postgresql.jdbc.PgArray, got ${other.getClass.getName}"))
    }
  )
  implicit lazy val arrayToStatement: ToStatement[Array[TypoOffsetDateTime]] = ToStatement[Array[TypoOffsetDateTime]]((s, index, v) => s.setArray(index, s.getConnection.createArrayOf("timestamptz", v.map(v => v.value.toString))))
  implicit lazy val bijection: Bijection[TypoOffsetDateTime, OffsetDateTime] = Bijection[TypoOffsetDateTime, OffsetDateTime](_.value)(TypoOffsetDateTime.apply)
  implicit lazy val column: Column[TypoOffsetDateTime] = Column.nonNull[TypoOffsetDateTime]((v1: Any, _) =>
    v1 match {
      case v: String => Right(TypoOffsetDateTime(OffsetDateTime.parse(v, parser)))
      case other => Left(TypeDoesNotMatch(s"Expected instance of java.lang.String, got ${other.getClass.getName}"))
    }
  )
  implicit def ordering(implicit O0: Ordering[OffsetDateTime]): Ordering[TypoOffsetDateTime] = Ordering.by(_.value)
  implicit lazy val parameterMetadata: ParameterMetaData[TypoOffsetDateTime] = new ParameterMetaData[TypoOffsetDateTime] {
    override def sqlType: String = "timestamptz"
    override def jdbcType: Int = Types.OTHER
  }
  implicit lazy val reads: Reads[TypoOffsetDateTime] = Reads.DefaultOffsetDateTimeReads.map(TypoOffsetDateTime.apply)
  implicit lazy val toStatement: ToStatement[TypoOffsetDateTime] = ToStatement[TypoOffsetDateTime]((s, index, v) => s.setObject(index, v.value.toString))
  implicit lazy val writes: Writes[TypoOffsetDateTime] = Writes.DefaultOffsetDateTimeWrites.contramap(_.value)
}
