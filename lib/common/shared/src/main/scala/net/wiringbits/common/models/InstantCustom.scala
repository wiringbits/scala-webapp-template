package net.wiringbits.common.models

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import play.api.libs.json.{Format, JsString, Json, Reads, Writes}

import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.{ChronoField, TemporalUnit}
import java.time.{Clock, Instant, OffsetDateTime}
import scala.util.{Failure, Success, Try}

// Typo doesn't support correctly java Instant, so we have to do our custom Instant
// TODO: remove and replace with java Instant when Typo support it
case class InstantCustom(value: Instant) {
  def plus(amountToAdd: Long, unit: TemporalUnit): InstantCustom = InstantCustom(value.plus(amountToAdd, unit))

  def isBefore(other: InstantCustom): Boolean = value.isBefore(other.value)

  def isAfter(other: InstantCustom): Boolean = value.isAfter(other.value)

  def plusSeconds(seconds: Long): InstantCustom = InstantCustom(value.plusSeconds(seconds))

  override def toString: String = value.toString
}

object InstantCustom {
  def now(): InstantCustom = InstantCustom(Instant.now())

  def fromClock(implicit clock: Clock): InstantCustom = InstantCustom(clock.instant())

  private val timestamptzParser: DateTimeFormatter = new DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm:ss")
    .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
    .appendPattern("X")
    .toFormatter

  implicit val instantCustomColumn: Column[InstantCustom] = Column.nonNull[InstantCustom] { (value, _) =>
    value match {
      case string: String =>
        Try(InstantCustom(OffsetDateTime.parse(string, timestamptzParser).toInstant)) match
          case Failure(_) => Left(TypeDoesNotMatch("Error parsing the instant"))
          case Success(value) => Right(value)
      case _ => Left(TypeDoesNotMatch("Error parsing the instant"))
    }
  }

  implicit val instantCustomOrdering: Ordering[InstantCustom] = Ordering.by(_.value)

  implicit val instantCustomToStatement: ToStatement[InstantCustom] =
    ToStatement[InstantCustom]((s, index, v) => s.setObject(index, v.value.toString))

  implicit val nameParameterMetaData: ParameterMetaData[InstantCustom] = new ParameterMetaData[InstantCustom] {
    override def sqlType: String = "TIMESTAMPTZ"

    override def jdbcType: Int = java.sql.Types.TIMESTAMP_WITH_TIMEZONE
  }

  implicit val instantCustomFormat: Format[Instant] = Format[Instant](
    fjs = implicitly[Reads[String]].map(string => Instant.parse(string)),
    tjs = Writes[Instant](i => JsString(i.toString))
  )

  implicit val instantCustomWrites: Writes[InstantCustom] = Writes[InstantCustom](i => Json.toJson(i.value))

  implicit val instantCustomReads: Reads[InstantCustom] =
    implicitly[Reads[String]].map(string => InstantCustom(Instant.parse(string)))
}
