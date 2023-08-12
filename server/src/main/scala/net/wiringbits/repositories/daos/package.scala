package net.wiringbits.repositories

import anorm.*
import anorm.SqlParser.*
import anorm.postgresql.*
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.models.jobs.{BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.models.*

import java.time.Instant
import java.util.UUID

package object daos {

  import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
  import org.postgresql.util.PGobject

  implicit val citextToString: Column[String] = Column.nonNull { case (value, meta) =>
    val MetaDataItem(qualified, _, clazz) = meta
    value match {
      case str: String => Right(str)
      case obj: PGobject if "citext" equalsIgnoreCase obj.getType => Right(obj.getValue)
      case _ =>
        Left(
          TypeDoesNotMatch(
            s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"
          )
        )
    }
  }

  implicit val nameParser: Column[Name] = Column.columnToString.map(Name.trusted)
  implicit val emailParser: Column[Email] = citextToString.map(Email.trusted)

  val userParser: RowParser[User] = {
    Macro.parser[User](
      "user_id",
      "name",
      "email",
      "password",
      "created_at",
      "verified_on"
    )
  }

  val userLogParser: RowParser[UserLog] = {
    Macro.parser[UserLog]("user_log_id", "user_id", "message", "created_at")
  }

  def enumColumn[A](f: String => Option[A]): Column[A] = Column.columnToString.mapResult { string =>
    f(string)
      .toRight(SqlRequestError(new RuntimeException(s"The value $string doesn't exists")))
  }

  implicit val tokenTypeColumn: Column[UserTokenType] = enumColumn(
    UserTokenType.withNameInsensitiveOption
  )

  // TODO: Use Macro.parser, for some reason it doesn't work so we have to parse it manually
  implicit val tokenParser: RowParser[UserToken] = {
    get[UUID]("user_token_id") ~
      str("token") ~
      get[UserTokenType]("token_type") ~
      get[Instant]("created_at") ~
      get[Instant]("expires_at") ~
      get[UUID]("user_id") map { case tokenId ~ token ~ tokenType ~ createdAt ~ expiresAt ~ userId =>
        UserToken(
          id = tokenId,
          tokenType = tokenType,
          token = token,
          createdAt = createdAt,
          expiresAt = expiresAt,
          userId = userId
        )
      }
  }

  implicit val backgroundJobStatusColumn: Column[BackgroundJobStatus] = enumColumn(
    BackgroundJobStatus.withNameInsensitiveOption
  )

  implicit val backgroundJobTypeColumn: Column[BackgroundJobType] = enumColumn(
    BackgroundJobType.withNameInsensitiveOption
  )

  implicit val backgroundJobParser: RowParser[BackgroundJobData] = {
    Macro.parser[BackgroundJobData](
      "background_job_id",
      "type",
      "payload",
      "status",
      "status_details",
      "error_count",
      "execute_at",
      "created_at",
      "updated_at"
    )
  }
}
