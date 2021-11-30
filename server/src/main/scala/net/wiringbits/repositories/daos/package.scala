package net.wiringbits.repositories

import anorm._
import net.wiringbits.repositories.models.{User, UserLog}

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

  val userParser: RowParser[User] = {
    Macro.parser[User](
      "user_id",
      "name",
      "email",
      "password",
      "created_at"
    )
  }

  val userLogParser: RowParser[UserLog] = {
    Macro.parser[UserLog]("user_log_id", "user_id", "message", "created_at")
  }
}
