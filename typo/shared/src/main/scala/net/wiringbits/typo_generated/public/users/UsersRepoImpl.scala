/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package net
package wiringbits
package typo_generated
package public
package users

import anorm.NamedParameter
import anorm.ParameterMetaData
import anorm.ParameterValue
import anorm.RowParser
import anorm.SQL
import anorm.SimpleSql
import anorm.SqlStringInterpolation
import anorm.ToStatement
import java.sql.Connection
import net.wiringbits.common.models.Email
import net.wiringbits.common.models.InstantCustom
import net.wiringbits.common.models.Name
import net.wiringbits.common.models.UUIDCustom
import net.wiringbits.typo_generated.customtypes.Defaulted
import typo.dsl.DeleteBuilder
import typo.dsl.SelectBuilder
import typo.dsl.SelectBuilderSql
import typo.dsl.UpdateBuilder

object UsersRepoImpl extends UsersRepo {
  override def delete(userId: /* user-picked */ UUIDCustom)(implicit c: Connection): Boolean = {
    SQL"""delete from public.users where "user_id" = ${ParameterValue(userId, null, implicitly[ToStatement[UUIDCustom]])}""".executeUpdate() > 0
  }
  override def delete: DeleteBuilder[UsersFields, UsersRow] = {
    DeleteBuilder("public.users", UsersFields)
  }
  override def insert(unsaved: UsersRow)(implicit c: Connection): UsersRow = {
    SQL"""insert into public.users("user_id", "name", "last_name", "email", "password", "created_at", "verified_on")
          values (${ParameterValue(unsaved.userId, null, implicitly[ToStatement[UUIDCustom]])}::uuid, ${ParameterValue(unsaved.name, null, implicitly[ToStatement[Name]])}, ${ParameterValue(unsaved.lastName, null, ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData))}, ${ParameterValue(unsaved.email, null, implicitly[ToStatement[Email]])}::citext, ${ParameterValue(unsaved.password, null, ToStatement.stringToStatement)}, ${ParameterValue(unsaved.createdAt, null, implicitly[ToStatement[InstantCustom]])}::timestamptz, ${ParameterValue(unsaved.verifiedOn, null, ToStatement.optionToStatement(implicitly[ToStatement[InstantCustom]], implicitly[ParameterMetaData[InstantCustom]]))}::timestamptz)
          returning "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
       """
      .executeInsert(UsersRow.rowParser(1).single)
    
  }
  override def insert(unsaved: UsersRowUnsaved)(implicit c: Connection): UsersRow = {
    val namedParameters = List(
      Some((NamedParameter("user_id", ParameterValue(unsaved.userId, null, implicitly[ToStatement[UUIDCustom]])), "::uuid")),
      Some((NamedParameter("name", ParameterValue(unsaved.name, null, implicitly[ToStatement[Name]])), "")),
      Some((NamedParameter("last_name", ParameterValue(unsaved.lastName, null, ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData))), "")),
      Some((NamedParameter("email", ParameterValue(unsaved.email, null, implicitly[ToStatement[Email]])), "::citext")),
      Some((NamedParameter("password", ParameterValue(unsaved.password, null, ToStatement.stringToStatement)), "")),
      Some((NamedParameter("verified_on", ParameterValue(unsaved.verifiedOn, null, ToStatement.optionToStatement(implicitly[ToStatement[InstantCustom]], implicitly[ParameterMetaData[InstantCustom]]))), "::timestamptz")),
      unsaved.createdAt match {
        case Defaulted.UseDefault => None
        case Defaulted.Provided(value) => Some((NamedParameter("created_at", ParameterValue(value, null, implicitly[ToStatement[InstantCustom]])), "::timestamptz"))
      }
    ).flatten
    val quote = '"'.toString
    if (namedParameters.isEmpty) {
      SQL"""insert into public.users default values
            returning "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
         """
        .executeInsert(UsersRow.rowParser(1).single)
    } else {
      val q = s"""insert into public.users(${namedParameters.map{case (x, _) => quote + x.name + quote}.mkString(", ")})
                  values (${namedParameters.map{ case (np, cast) => s"{${np.name}}$cast"}.mkString(", ")})
                  returning "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
               """
      SimpleSql(SQL(q), namedParameters.map { case (np, _) => np.tupled }.toMap, RowParser.successful)
        .executeInsert(UsersRow.rowParser(1).single)
    }
    
  }
  override def select: SelectBuilder[UsersFields, UsersRow] = {
    SelectBuilderSql("public.users", UsersFields, UsersRow.rowParser)
  }
  override def selectAll(implicit c: Connection): List[UsersRow] = {
    SQL"""select "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
          from public.users
       """.as(UsersRow.rowParser(1).*)
  }
  override def selectById(userId: /* user-picked */ UUIDCustom)(implicit c: Connection): Option[UsersRow] = {
    SQL"""select "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
          from public.users
          where "user_id" = ${ParameterValue(userId, null, implicitly[ToStatement[UUIDCustom]])}
       """.as(UsersRow.rowParser(1).singleOpt)
  }
  override def selectByIds(userIds: Array[/* user-picked */ UUIDCustom])(implicit c: Connection, toStatement: ToStatement[Array[/* user-picked */ UUIDCustom]]): List[UsersRow] = {
    SQL"""select "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
          from public.users
          where "user_id" = ANY(${userIds})
       """.as(UsersRow.rowParser(1).*)
    
  }
  override def selectByUnique(email: /* user-picked */ Email)(implicit c: Connection): Option[UsersRow] = {
    SQL"""select "email"::text
          from public.users
          where "email" = ${ParameterValue(email, null, implicitly[ToStatement[Email]])}
       """.as(UsersRow.rowParser(1).singleOpt)
    
  }
  override def update(row: UsersRow)(implicit c: Connection): Boolean = {
    val userId = row.userId
    SQL"""update public.users
          set "name" = ${ParameterValue(row.name, null, implicitly[ToStatement[Name]])},
              "last_name" = ${ParameterValue(row.lastName, null, ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData))},
              "email" = ${ParameterValue(row.email, null, implicitly[ToStatement[Email]])}::citext,
              "password" = ${ParameterValue(row.password, null, ToStatement.stringToStatement)},
              "created_at" = ${ParameterValue(row.createdAt, null, implicitly[ToStatement[InstantCustom]])}::timestamptz,
              "verified_on" = ${ParameterValue(row.verifiedOn, null, ToStatement.optionToStatement(implicitly[ToStatement[InstantCustom]], implicitly[ParameterMetaData[InstantCustom]]))}::timestamptz
          where "user_id" = ${ParameterValue(userId, null, implicitly[ToStatement[UUIDCustom]])}
       """.executeUpdate() > 0
  }
  override def update: UpdateBuilder[UsersFields, UsersRow] = {
    UpdateBuilder("public.users", UsersFields, UsersRow.rowParser)
  }
  override def upsert(unsaved: UsersRow)(implicit c: Connection): UsersRow = {
    SQL"""insert into public.users("user_id", "name", "last_name", "email", "password", "created_at", "verified_on")
          values (
            ${ParameterValue(unsaved.userId, null, implicitly[ToStatement[UUIDCustom]])}::uuid,
            ${ParameterValue(unsaved.name, null, implicitly[ToStatement[Name]])},
            ${ParameterValue(unsaved.lastName, null, ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData))},
            ${ParameterValue(unsaved.email, null, implicitly[ToStatement[Email]])}::citext,
            ${ParameterValue(unsaved.password, null, ToStatement.stringToStatement)},
            ${ParameterValue(unsaved.createdAt, null, implicitly[ToStatement[InstantCustom]])}::timestamptz,
            ${ParameterValue(unsaved.verifiedOn, null, ToStatement.optionToStatement(implicitly[ToStatement[InstantCustom]], implicitly[ParameterMetaData[InstantCustom]]))}::timestamptz
          )
          on conflict ("user_id")
          do update set
            "name" = EXCLUDED."name",
            "last_name" = EXCLUDED."last_name",
            "email" = EXCLUDED."email",
            "password" = EXCLUDED."password",
            "created_at" = EXCLUDED."created_at",
            "verified_on" = EXCLUDED."verified_on"
          returning "user_id", "name", "last_name", "email"::text, "password", "created_at"::text, "verified_on"::text
       """
      .executeInsert(UsersRow.rowParser(1).single)
    
  }
}
