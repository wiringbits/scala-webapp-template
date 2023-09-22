package net.wiringbits.repositories

// Do not import packages, because it is easier to move this code to typo package without imports
object TypoCodecs {
  implicit def wrappedColumn[T <: net.wiringbits.webapp.common.models.WrappedString](implicit
      f: String => T
  ): anorm.Column[T] =
    anorm.Column.nonNull[T] { (value, _) =>
      value match {
        case string: String => Right(f(string))
        case _ => Left(anorm.TypeDoesNotMatch("Error parsing the email"))
      }
    }

  implicit def wrappedOrdering[T <: net.wiringbits.webapp.common.models.WrappedString]: scala.math.Ordering[T] =
    scala.math.Ordering.by(_.string)

  implicit def wrappedToStatement[T <: net.wiringbits.webapp.common.models.WrappedString]: anorm.ToStatement[T] =
    anorm.ToStatement[T]((s, index, v) => s.setObject(index, v.string))

  implicit def wrappedParameterMetaData[T <: net.wiringbits.webapp.common.models.WrappedString](implicit
      customSqlType: String = "VARCHAR"
  ): anorm.ParameterMetaData[T] = new anorm.ParameterMetaData[T] {
    override def sqlType: String = customSqlType

    override def jdbcType: Int = java.sql.Types.OTHER
  }

  implicit def idColumn[T <: net.wiringbits.common.models.id.Id](implicit f: String => T): anorm.Column[T] =
    anorm.Column.nonNull[T] { (value, _) =>
      value match {
        case string: String => Right(f(string))
        case _ => Left(anorm.TypeDoesNotMatch("Error parsing the email"))
      }
    }

  implicit def idOrdering[T <: net.wiringbits.common.models.id.Id]: Ordering[T] = Ordering.by(_.value)

  implicit def idToStatement[T <: net.wiringbits.common.models.id.Id]: anorm.ToStatement[T] =
    anorm.ToStatement[T]((s, index, v) => s.setObject(index, v.value))

  implicit def idParameterMetaData[T <: net.wiringbits.common.models.id.Id](implicit
      customSqlType: String
  ): anorm.ParameterMetaData[T] = new anorm.ParameterMetaData[T] {
    override def sqlType: String = customSqlType

    override def jdbcType: Int = java.sql.Types.OTHER
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  private def timestamp[T](ts: java.sql.Timestamp)(f: java.sql.Timestamp => T): Either[anorm.SqlRequestError, T] =
    Right(
      if (ts == null) null.asInstanceOf[T] else f(ts)
    )

  private val timestamptzParser: java.time.format.DateTimeFormatter = new java.time.format.DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm:ss")
    .appendFraction(java.time.temporal.ChronoField.MICRO_OF_SECOND, 0, 6, true)
    .appendPattern("X")
    .toFormatter
  implicit val columnToInstant: anorm.Column[net.wiringbits.common.models.InstantCustom] =
    anorm.Column.nonNull(instantValueTo(instantToInstantCustom))

  private def instantToInstantCustom(instant: java.time.Instant): net.wiringbits.common.models.InstantCustom =
    net.wiringbits.common.models.InstantCustom(instant)

  private def instantValueTo(
      epoch: java.time.Instant => net.wiringbits.common.models.InstantCustom
  )(value: Any, meta: anorm.MetaDataItem): Either[anorm.SqlRequestError, net.wiringbits.common.models.InstantCustom] = {
    value match {
      case date: java.time.LocalDateTime => Right(epoch(date.toInstant(java.time.ZoneOffset.UTC)))
      case ts: java.sql.Timestamp => timestamp(ts)(t => epoch(t.toInstant))
      case date: java.util.Date =>
        Right(epoch(java.time.Instant.ofEpochMilli(date.getTime)))
      case time: Long =>
        Right(epoch(java.time.Instant.ofEpochMilli(time)))
      case anorm.TimestampWrapper1(ts) => timestamp(ts)(t => epoch(t.toInstant))
      case anorm.TimestampWrapper2(ts) => timestamp(ts)(t => epoch(t.toInstant))
      case string: String =>
        scala.util.Try(
          net.wiringbits.common.models
            .InstantCustom(java.time.OffsetDateTime.parse(string, timestamptzParser).toInstant)
        ) match
          case scala.util.Failure(_) => Left(anorm.TypeDoesNotMatch("Error parsing the instant"))
          case scala.util.Success(value) => Right(value)
      case _ =>
        Left(anorm.TypeDoesNotMatch("Error parsing the instant"))
    }
  }

  implicit val instantCustomOrdering: Ordering[net.wiringbits.common.models.InstantCustom] = Ordering.by(_.value)
  implicit val instantCustomToStatement: anorm.ToStatement[net.wiringbits.common.models.InstantCustom] =
    anorm.ToStatement[net.wiringbits.common.models.InstantCustom]((s, index, v) => s.setObject(index, v.value.toString))
  implicit val instantParameterMetaData: anorm.ParameterMetaData[net.wiringbits.common.models.InstantCustom] =
    new anorm.ParameterMetaData[net.wiringbits.common.models.InstantCustom] {
      override def sqlType: String = "TIMESTAMPTZ"

      override def jdbcType: Int = java.sql.Types.TIMESTAMP_WITH_TIMEZONE
    }

  implicit def enumJobTypeColumn[T <: enumeratum.EnumEntry](implicit
      withNameInsensitiveOption: String => Option[T]
  ): anorm.Column[T] =
    anorm.Column.nonNull[T] { (value, _) =>
      value match {
        case string: String =>
          withNameInsensitiveOption(string) match
            case Some(value) => Right(value)
            case None => Left(anorm.TypeDoesNotMatch(s"Unknown enum: $string"))
        case _ => Left(anorm.TypeDoesNotMatch("Error parsing the enum"))
      }
    }

  implicit def enumOrdering[T <: enumeratum.EnumEntry]: scala.math.Ordering[T] =
    scala.math.Ordering.by(_.entryName)

  implicit def enumToStatement[T <: enumeratum.EnumEntry]: anorm.ToStatement[T] =
    anorm.ToStatement[T]((s, index, v) => s.setObject(index, v.entryName))

  implicit def enumParameterMetaData[T <: enumeratum.EnumEntry]: anorm.ParameterMetaData[T] =
    new anorm.ParameterMetaData[T] {
      override def sqlType: String = "TEXT"

      override def jdbcType: Int = java.sql.Types.VARCHAR
    }
}
