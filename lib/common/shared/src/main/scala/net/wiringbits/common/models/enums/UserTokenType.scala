package net.wiringbits.common.models.enums

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import enumeratum.*
import enumeratum.EnumEntry.Uppercase
import play.api.libs.json.{Format, JsString, Reads, Writes}

sealed trait UserTokenType extends EnumEntry with Uppercase

object UserTokenType extends Enum[UserTokenType] {
  case object EmailVerification extends UserTokenType
  case object ResetPassword extends UserTokenType

  val values: IndexedSeq[UserTokenType] = findValues

  implicit val userTokenTypeColumn: Column[UserTokenType] = Column.nonNull[UserTokenType] { (value, _) =>
    value match {
      case string: String =>
        withNameInsensitiveOption(string) match
          case Some(value) => Right(value)
          case None => Left(TypeDoesNotMatch(s"Unknown user token type: $string"))
      case _ => Left(TypeDoesNotMatch("Error parsing the user token type"))
    }
  }

  implicit val userTokenTypeOrdering: Ordering[UserTokenType] = Ordering.by(_.entryName)

  implicit val userTokenTypeToStatement: ToStatement[UserTokenType] =
    ToStatement[UserTokenType]((s, index, v) => s.setObject(index, v.entryName))

  implicit val userTokenTypeEmailParameterMetaData: ParameterMetaData[UserTokenType] =
    new ParameterMetaData[UserTokenType] {
      override def sqlType: String = "TEXT"

      override def jdbcType: Int = java.sql.Types.VARCHAR
    }

  implicit val userTokenTypeCustomFormat: Format[UserTokenType] = Format[UserTokenType](
    fjs = implicitly[Reads[String]].map(string => withNameInsensitive(string)),
    tjs = Writes[UserTokenType](i => JsString(i.entryName))
  )

  implicit val userTokenTypeCustomReads: Reads[UserTokenType] =
    implicitly[Reads[String]].map(string => withNameInsensitive(string))

  implicit val userTokenTypeCustomWrites: Writes[UserTokenType] = Writes[UserTokenType](i => JsString(i.entryName))
}
