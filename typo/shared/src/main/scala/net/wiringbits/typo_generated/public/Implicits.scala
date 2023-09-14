package net.wiringbits.typo_generated.public

import anorm.{Column, TypeDoesNotMatch}

import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField
import java.time.{Instant, OffsetDateTime}

// TODO: this should be parsed by Typo and Anorm but the current parse doesn't work
object Implicits {
  private val timestamptzParser: DateTimeFormatter = new DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm:ss")
    .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
    .appendPattern("X")
    .toFormatter

  implicit val instantColumn: Column[Instant] = Column.nonNull[Instant]((v1: Any, _) =>
    v1 match {
      case v: String => Right(OffsetDateTime.parse(v, timestamptzParser).toInstant)
      case other =>
        Left(TypeDoesNotMatch(s"Expected instance of org.postgresql.util.PGobject, got ${other.getClass.getName}"))
    }
  )
}
