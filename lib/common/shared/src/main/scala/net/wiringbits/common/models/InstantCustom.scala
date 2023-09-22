package net.wiringbits.common.models

import play.api.libs.json.*

import java.time.*
import java.time.temporal.TemporalUnit

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

  implicit val instantCustomFormat: Format[Instant] = Format[Instant](
    fjs = implicitly[Reads[String]].map(string => Instant.parse(string)),
    tjs = Writes[Instant](i => JsString(i.toString))
  )

  implicit val instantCustomWrites: Writes[InstantCustom] = Writes[InstantCustom](i => Json.toJson(i.value))

  implicit val instantCustomReads: Reads[InstantCustom] =
    implicitly[Reads[String]].map(string => InstantCustom(Instant.parse(string)))
}
