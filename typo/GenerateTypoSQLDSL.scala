//> using scala "3.3.0"

//> using lib "com.olvind.typo::typo:0.3.1"
//> using lib "com.olvind.typo::typo-dsl-anorm:0.3.1"
//> using lib "com.typesafe.play::play-json::2.10.0-RC9"
//> using lib "com.lihaoyi::os-lib:0.9.1"

import typo.*
import typo.db.*
import os.*

object TypeOverrides {
  private val emailTypeImport = "net.wiringbits.common.models.Email"
  private val nameTypeImport = "net.wiringbits.common.models.Name"
  private val instantTypeImport = "net.wiringbits.common.models.InstantCustom"
  private val userTokenTypeImport = "net.wiringbits.common.models.enums.UserTokenType"
  private val backgroundJobTypeImport = "net.wiringbits.common.models.enums.BackgroundJobType"
  private val backgroundJobStatusImport = "net.wiringbits.common.models.enums.BackgroundJobStatus"
  private def idImport(name: String) = s"net.wiringbits.common.models.id.$name"

  def apply(): TypeOverride = usersOverrides
    .orElse(userTokensOverrides)
    .orElse(backgroundJobsOverrides)
    .orElse(userLogsOverrides)

  private val usersOverrides = {
    val relationName = RelationName(Some("public"), "users")

    TypeOverride.of {
      case (relationName, ColName("user_id")) => idImport("UserId")
      case (relationName, ColName("email")) => emailTypeImport
      case (relationName, ColName("name")) => nameTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("verified_on")) => instantTypeImport
    }
  }

  private val userTokensOverrides = {
    val relationName = RelationName(Some("public"), "user_tokens")

    TypeOverride.of {
      case (relationName, ColName("user_token_id")) => idImport("UserTokenId")
      case (relationName, ColName("token_type")) => userTokenTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("expires_at")) => instantTypeImport
    }
  }

  private val backgroundJobsOverrides = {
    val relationName = RelationName(Some("public"), "background_jobs")

    TypeOverride.of {
      case (relationName, ColName("background_job_id")) => idImport("BackgroundJobId")
      case (relationName, ColName("type")) => backgroundJobTypeImport
      case (relationName, ColName("status")) => backgroundJobStatusImport
      case (relationName, ColName("execute_at")) => instantTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("updated_at")) => instantTypeImport
    }
  }

  private val userLogsOverrides = {
    val relationName = RelationName(Some("public"), "user_logs")

    TypeOverride.of {
      case (relationName, ColName("user_log_id")) => idImport("UserLogId")
      case (relationName, ColName("created_at")) => instantTypeImport
    }
  }
}

object GenerateTypoSQLDSL extends App {
  given conn: java.sql.Connection =
    // TODO: get connection from config
    java.sql.DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/wiringbits_db?user=postgres&password=postgres"
    )

  // current folder, where you run the script from
  val pwd = os.pwd

  // destination folder. All files in this dir will be overwritten!
  val targetDir = pwd / "typo" / "shared" / "src" / "main" / "scala"

  // where typo will look for sql files
  val sqlScriptsFolder = pwd / "server" / "src" / "main" / "resources" / "sql"

  val pkg = "net.wiringbits.typo_generated"

  def runScript = {
    // adapt to your instance and credentials
    val options = Options(
      pkg = pkg,
      dbLib = Some(DbLibName.Anorm),
      jsonLibs = List(JsonLibName.PlayJson),
      enableDsl = true,
      typeOverride = TypeOverrides()
    )

    generateFromDb(options, scriptsPaths = List(sqlScriptsFolder.toNIO))
      .overwriteFolder(folder = targetDir.toNIO)
  }

  def addCustomCodecs = {
    // TODO: get from server
    val str =
      """
        |implicit def wrappedColumn[T <: net.wiringbits.webapp.common.models.WrappedString](implicit
        |      f: String => T
        |  ): anorm.Column[T] =
        |    anorm.Column.nonNull[T] { (value, _) =>
        |      value match {
        |        case string: String => Right(f(string))
        |        case _ => Left(anorm.TypeDoesNotMatch("Error parsing the email"))
        |      }
        |    }
        |  implicit def wrappedOrdering[T <: net.wiringbits.webapp.common.models.WrappedString]: scala.math.Ordering[T] =
        |    scala.math.Ordering.by(_.string)
        |  implicit def wrappedToStatement[T <: net.wiringbits.webapp.common.models.WrappedString]: anorm.ToStatement[T] =
        |    anorm.ToStatement[T]((s, index, v) => s.setObject(index, v.string))
        |  implicit def wrappedParameterMetaData[T <: net.wiringbits.webapp.common.models.WrappedString](implicit
        |      customSqlType: String = "VARCHAR"
        |  ): anorm.ParameterMetaData[T] = new anorm.ParameterMetaData[T] {
        |    override def sqlType: String = customSqlType
        |
        |    override def jdbcType: Int = java.sql.Types.OTHER
        |  }
        |  implicit def idColumn[T <: net.wiringbits.common.models.id.Id](implicit f: String => T): anorm.Column[T] =
        |    anorm.Column.nonNull[T] { (value, _) =>
        |      value match {
        |        case string: String => Right(f(string))
        |        case _ => Left(anorm.TypeDoesNotMatch("Error parsing the email"))
        |      }
        |    }
        |  implicit def idOrdering[T <: net.wiringbits.common.models.id.Id]: Ordering[T] = Ordering.by(_.value)
        |  implicit def idToStatement[T <: net.wiringbits.common.models.id.Id]: anorm.ToStatement[T] =
        |    anorm.ToStatement[T]((s, index, v) => s.setObject(index, v.value))
        |  implicit def idParameterMetaData[T <: net.wiringbits.common.models.id.Id](implicit
        |      customSqlType: String
        |  ): anorm.ParameterMetaData[T] = new anorm.ParameterMetaData[T] {
        |    override def sqlType: String = customSqlType
        |
        |    override def jdbcType: Int = java.sql.Types.OTHER
        |  }
        |  @SuppressWarnings(Array("org.wartremover.warts.Null"))
        |  private def timestamp[T](ts: java.sql.Timestamp)(f: java.sql.Timestamp => T): Either[anorm.SqlRequestError, T] =
        |    Right(
        |      if (ts == null) null.asInstanceOf[T] else f(ts)
        |    )
        |  private val timestamptzParser: java.time.format.DateTimeFormatter = new java.time.format.DateTimeFormatterBuilder()
        |    .appendPattern("yyyy-MM-dd HH:mm:ss")
        |    .appendFraction(java.time.temporal.ChronoField.MICRO_OF_SECOND, 0, 6, true)
        |    .appendPattern("X")
        |    .toFormatter
        |  implicit val columnToInstant: anorm.Column[net.wiringbits.common.models.InstantCustom] =
        |    anorm.Column.nonNull(instantValueTo(instantToInstantCustom))
        |  private def instantToInstantCustom(instant: java.time.Instant): net.wiringbits.common.models.InstantCustom =
        |    net.wiringbits.common.models.InstantCustom(instant)
        |  private def instantValueTo(
        |      epoch: java.time.Instant => net.wiringbits.common.models.InstantCustom
        |  )(value: Any, meta: anorm.MetaDataItem): Either[anorm.SqlRequestError, net.wiringbits.common.models.InstantCustom] = {
        |    value match {
        |      case date: java.time.LocalDateTime => Right(epoch(date.toInstant(java.time.ZoneOffset.UTC)))
        |      case ts: java.sql.Timestamp => timestamp(ts)(t => epoch(t.toInstant))
        |      case date: java.util.Date =>
        |        Right(epoch(java.time.Instant.ofEpochMilli(date.getTime)))
        |      case time: Long =>
        |        Right(epoch(java.time.Instant.ofEpochMilli(time)))
        |      case anorm.TimestampWrapper1(ts) => timestamp(ts)(t => epoch(t.toInstant))
        |      case anorm.TimestampWrapper2(ts) => timestamp(ts)(t => epoch(t.toInstant))
        |      case string: String =>
        |        scala.util.Try(
        |          net.wiringbits.common.models
        |            .InstantCustom(java.time.OffsetDateTime.parse(string, timestamptzParser).toInstant)
        |        ) match
        |          case scala.util.Failure(_) => Left(anorm.TypeDoesNotMatch("Error parsing the instant"))
        |          case scala.util.Success(value) => Right(value)
        |      case _ =>
        |        Left(anorm.TypeDoesNotMatch("Error parsing the instant"))
        |    }
        |  }
        |  implicit val instantCustomOrdering: Ordering[net.wiringbits.common.models.InstantCustom] = Ordering.by(_.value)
        |  implicit val instantCustomToStatement: anorm.ToStatement[net.wiringbits.common.models.InstantCustom] =
        |    anorm.ToStatement[net.wiringbits.common.models.InstantCustom]((s, index, v) => s.setObject(index, v.value.toString))
        |  implicit val instantParameterMetaData: anorm.ParameterMetaData[net.wiringbits.common.models.InstantCustom] =
        |    new anorm.ParameterMetaData[net.wiringbits.common.models.InstantCustom] {
        |      override def sqlType: String = "TIMESTAMPTZ"
        |
        |      override def jdbcType: Int = java.sql.Types.TIMESTAMP_WITH_TIMEZONE
        |    }
        |  implicit def enumJobTypeColumn[T <: enumeratum.EnumEntry](implicit
        |      withNameInsensitiveOption: String => Option[T]
        |  ): anorm.Column[T] =
        |    anorm.Column.nonNull[T] { (value, _) =>
        |      value match {
        |        case string: String =>
        |          withNameInsensitiveOption(string) match
        |            case Some(value) => Right(value)
        |            case None => Left(anorm.TypeDoesNotMatch(s"Unknown enum: $string"))
        |        case _ => Left(anorm.TypeDoesNotMatch("Error parsing the enum"))
        |      }
        |    }
        |  implicit def enumOrdering[T <: enumeratum.EnumEntry]: scala.math.Ordering[T] =
        |    scala.math.Ordering.by(_.entryName)
        |  implicit def enumToStatement[T <: enumeratum.EnumEntry]: anorm.ToStatement[T] =
        |    anorm.ToStatement[T]((s, index, v) => s.setObject(index, v.entryName))
        |  implicit def enumParameterMetaData[T <: enumeratum.EnumEntry]: anorm.ParameterMetaData[T] =
        |    new anorm.ParameterMetaData[T] {
        |      override def sqlType: String = "TEXT"
        |
        |      override def jdbcType: Int = java.sql.Types.VARCHAR
        |    }
        |}
        |""".stripMargin

    val dir = targetDir / os.RelPath(pkg.replace('.', '/')) / "package.scala"
    // - 2 because we want to remove the '\n' at the end of the file and the last '}'
    os.truncate(dir, os.size(dir) - 2)
    os.write.append(dir, str)
  }

  runScript
  addCustomCodecs
}
