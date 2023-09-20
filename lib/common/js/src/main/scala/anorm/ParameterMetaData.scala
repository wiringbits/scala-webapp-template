package anorm

import java.sql.Types
import java.time.Instant
import java.util.UUID

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
trait ParameterMetaData[T] {
  def sqlType: String

  def jdbcType: Int
}

object ParameterMetaData {
  implicit object StringParameterMetaData extends ParameterMetaData[String] {
    val sqlType = "VARCHAR"
    val jdbcType = Types.VARCHAR
  }

  implicit object IntParameterMetaData extends ParameterMetaData[Int] {
    val sqlType = "INTEGER"
    val jdbcType = Types.INTEGER
  }

  implicit object InstantParameterMetaData extends ParameterMetaData[Instant] {
    val sqlType = "TIMESTAMP"
    val jdbcType = Types.TIMESTAMP
  }

  implicit object UUIDParameterMetaData extends ParameterMetaData[UUID] {
    val sqlType = StringParameterMetaData.sqlType
    val jdbcType = StringParameterMetaData.jdbcType
  }
}
