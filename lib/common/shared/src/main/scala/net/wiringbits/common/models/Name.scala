package net.wiringbits.common.models

import anorm.{Column, ParameterMetaData, ToStatement, TypeDoesNotMatch}
import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class Name private (val string: String) extends WrappedString

object Name extends WrappedString.Companion[Name] {

  private val minNameLength: Int = 2 // we do have people named like `Jo`

  override def validate(string: String): ValidationResult[Name] = {
    val isValid = string.length >= minNameLength

    Option
      .when(isValid)(ValidationResult.Valid(string, new Name(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid name")
      }
  }

  override def trusted(string: String): Name = new Name(string)

  implicit val nameColumn: Column[Name] = Column.nonNull[Name] { (value, _) =>
    value match {
      case string: String => Right(trusted(string))
      case _ => Left(TypeDoesNotMatch("Error parsing the email"))
    }
  }

  implicit val nameOrdering: Ordering[Name] = Ordering.by(_.string)

  implicit val nameToStatement: ToStatement[Name] =
    ToStatement[Name]((s, index, v) => s.setObject(index, v.string))

  implicit val nameParameterMetaData: ParameterMetaData[Name] = new ParameterMetaData[Name] {
    override def sqlType: String = "VARCHAR"

    override def jdbcType: Int = java.sql.Types.VARCHAR
  }
}
