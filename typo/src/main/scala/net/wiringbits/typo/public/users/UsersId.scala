/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package net
package wiringbits
package typo
package public
package users

import anorm.Column
import anorm.ParameterMetaData
import anorm.ToStatement
import net.wiringbits.typo.customtypes.TypoUUID
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import typo.dsl.Bijection

/** Type for the primary key of table `public.users` */
case class UsersId(value: TypoUUID) extends AnyVal
object UsersId {
  implicit lazy val arrayColumn: Column[Array[UsersId]] = Column.columnToArray(column, implicitly)
  implicit lazy val arrayToStatement: ToStatement[Array[UsersId]] = TypoUUID.arrayToStatement.contramap(_.map(_.value))
  implicit lazy val bijection: Bijection[UsersId, TypoUUID] = Bijection[UsersId, TypoUUID](_.value)(UsersId.apply)
  implicit lazy val column: Column[UsersId] = TypoUUID.column.map(UsersId.apply)
  implicit def ordering(implicit O0: Ordering[TypoUUID]): Ordering[UsersId] = Ordering.by(_.value)
  implicit lazy val parameterMetadata: ParameterMetaData[UsersId] = new ParameterMetaData[UsersId] {
    override def sqlType: String = TypoUUID.parameterMetadata.sqlType
    override def jdbcType: Int = TypoUUID.parameterMetadata.jdbcType
  }
  implicit lazy val reads: Reads[UsersId] = TypoUUID.reads.map(UsersId.apply)
  implicit lazy val toStatement: ToStatement[UsersId] = TypoUUID.toStatement.contramap(_.value)
  implicit lazy val writes: Writes[UsersId] = TypoUUID.writes.contramap(_.value)
}
