package net.wiringbits.common.models

import anorm.*
import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class Email private (val string: String) extends WrappedString

object Email extends WrappedString.Companion[Email] {

  private val emailRegex =
    """^[\w.!#$%&'*+/=?^_`{|}~-]+@([\w-]+\.)+[\w-]{2,7}$""".r

  override def validate(string: String): ValidationResult[Email] = {
    val valid = emailRegex.findAllMatchIn(string).length == 1
    Option
      .when(valid)(ValidationResult.Valid(string, new Email(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid email")
      }
  }

  override def trusted(string: String): Email = new Email(string)

  implicit val column: Column[Email] = Column.nonNull[Email] { (value, _) =>
    value match {
      case string: String => Right(trusted(string))
      case _ => Left(TypeDoesNotMatch("Error parsing the email"))
    }
  }

  implicit val ordering: Ordering[Email] = Ordering.by(_.string)

  implicit val toStatement: ToStatement[Email] = ToStatement[Email]((s, index, v) => s.setObject(index, v.string))

  implicit val emailParameterMetaData: ParameterMetaData[Email] = new ParameterMetaData[Email] {
    override def sqlType: String = "CITEXT"

    override def jdbcType: Int = java.sql.Types.OTHER
  }
}
