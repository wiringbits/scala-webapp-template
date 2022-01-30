package net.wiringbits.repositories

import anorm._
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.models.{
  NotificationStatus,
  NotificationType,
  User,
  UserLog,
  UserNotification,
  UserToken,
  UserTokenType
}

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

  implicit val notificationStatusColumn: Column[NotificationStatus] = enumColumn(
    NotificationStatus.withNameInsensitiveOption
  )

  implicit val notificationTypeColumn: Column[NotificationType] = enumColumn(NotificationType.withNameInsensitiveOption)

  implicit val tokenParser: RowParser[UserToken] = {
    Macro.parser[UserToken]("user_token_id", "token", "token_type", "created_at", "expires_at", "user_id")
  }

  implicit val userNotificationParser: RowParser[UserNotification] = {
    Macro.parser[UserNotification](
      "user_notification_id",
      "user_id",
      "notification_type",
      "subject",
      "message",
      "status",
      "status_details",
      "error_count",
      "execute_at",
      "created_at",
      "updated_at"
    )
  }
}
