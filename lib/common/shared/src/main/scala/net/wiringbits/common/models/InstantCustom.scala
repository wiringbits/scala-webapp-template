package net.wiringbits.common.models

import anorm.*
import play.api.libs.json.*

import java.sql.Timestamp
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.{ChronoField, TemporalUnit}
import java.time.*
import java.util.Date
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

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  private def timestamp[T](ts: Timestamp)(f: Timestamp => T): Either[SqlRequestError, T] = Right(
    if (ts == null) null.asInstanceOf[T] else f(ts)
  )

  implicit val columnToInstant: Column[InstantCustom] = Column.nonNull(instantValueTo(instantToInstantCustom))

  private def instantToInstantCustom(instant: Instant): InstantCustom = InstantCustom(instant)

  private def instantValueTo(
      epoch: Instant => InstantCustom
  )(value: Any, meta: MetaDataItem): Either[SqlRequestError, InstantCustom] = {
    value match {
      case date: LocalDateTime => Right(epoch(date.toInstant(ZoneOffset.UTC)))
      case ts: java.sql.Timestamp => timestamp(ts)(t => epoch(t.toInstant))
      case date: java.util.Date =>
        Right(epoch(Instant.ofEpochMilli(date.getTime)))
      case time: Long =>
        Right(epoch(Instant.ofEpochMilli(time)))
      case TimestampWrapper1(ts) => timestamp(ts)(t => epoch(t.toInstant))
      case TimestampWrapper2(ts) => timestamp(ts)(t => epoch(t.toInstant))
      case string: String =>
        Try(InstantCustom(OffsetDateTime.parse(string, timestamptzParser).toInstant)) match
          case Failure(_) => Left(TypeDoesNotMatch("Error parsing the instant"))
          case Success(value) => Right(value)
      case _ =>
        Left(TypeDoesNotMatch("Error parsing the instant"))
    }
  }

  implicit val instantCustomOrdering: Ordering[InstantCustom] = Ordering.by(_.value)

  implicit val instantCustomToStatement: ToStatement[InstantCustom] =
    ToStatement[InstantCustom]((s, index, v) => s.setObject(index, v.value.toString))

  implicit val instantParameterMetaData: ParameterMetaData[InstantCustom] = new ParameterMetaData[InstantCustom] {
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
