/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package net
package wiringbits
package typo_generated
package customtypes

import anorm.Column
import anorm.ParameterMetaData
import anorm.ToStatement
import anorm.TypeDoesNotMatch
import java.sql.Types
import org.postgresql.jdbc.PgArray
import org.postgresql.util.PGobject
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import typo.dsl.Bijection

/** jsonb (via PGObject) */
case class TypoJsonb(value: String)

object TypoJsonb {
  implicit lazy val arrayColumn: Column[Array[TypoJsonb]] = Column.nonNull[Array[TypoJsonb]]((v1: Any, _) =>
    v1 match {
        case v: PgArray =>
         v.getArray match {
           case v: Array[?] =>
             Right(v.map(v => TypoJsonb(v.asInstanceOf[String])))
           case other => Left(TypeDoesNotMatch(s"Expected one-dimensional array from JDBC to produce an array of TypoJsonb, got ${other.getClass.getName}"))
         }
      case other => Left(TypeDoesNotMatch(s"Expected instance of org.postgresql.jdbc.PgArray, got ${other.getClass.getName}"))
    }
  )
  implicit lazy val arrayToStatement: ToStatement[Array[TypoJsonb]] = ToStatement[Array[TypoJsonb]]((s, index, v) => s.setArray(index, s.getConnection.createArrayOf("jsonb", v.map(v => {
                                                                                                                       val obj = new PGobject
                                                                                                                       obj.setType("jsonb")
                                                                                                                       obj.setValue(v.value)
                                                                                                                       obj
                                                                                                                     }))))
  implicit lazy val bijection: Bijection[TypoJsonb, String] = Bijection[TypoJsonb, String](_.value)(TypoJsonb.apply)
  implicit lazy val column: Column[TypoJsonb] = Column.nonNull[TypoJsonb]((v1: Any, _) =>
    v1 match {
      case v: PGobject => Right(TypoJsonb(v.getValue))
      case other => Left(TypeDoesNotMatch(s"Expected instance of org.postgresql.util.PGobject, got ${other.getClass.getName}"))
    }
  )
  implicit lazy val ordering: Ordering[TypoJsonb] = Ordering.by(_.value)
  implicit lazy val parameterMetadata: ParameterMetaData[TypoJsonb] = new ParameterMetaData[TypoJsonb] {
    override def sqlType: String = "jsonb"
    override def jdbcType: Int = Types.OTHER
  }
  implicit lazy val reads: Reads[TypoJsonb] = Reads.StringReads.map(TypoJsonb.apply)
  implicit lazy val toStatement: ToStatement[TypoJsonb] = ToStatement[TypoJsonb]((s, index, v) => s.setObject(index, {
                                                               val obj = new PGobject
                                                               obj.setType("jsonb")
                                                               obj.setValue(v.value)
                                                               obj
                                                             }))
  implicit lazy val writes: Writes[TypoJsonb] = Writes.StringWrites.contramap(_.value)
}
