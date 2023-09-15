/** File has been automatically generated by `typo`.
  *
  * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
  */
package net
package wiringbits
package typo_generated
package public
package user_logs

import anorm.NamedParameter
import anorm.ParameterValue
import anorm.RowParser
import anorm.SQL
import anorm.SimpleSql
import anorm.SqlStringInterpolation
import anorm.ToStatement
import java.sql.Connection
import net.wiringbits.common.models.InstantCustom
import net.wiringbits.common.models.UUIDCustom
import net.wiringbits.typo_generated.customtypes.Defaulted
import typo.dsl.DeleteBuilder
import typo.dsl.SelectBuilder
import typo.dsl.SelectBuilderSql
import typo.dsl.UpdateBuilder

object UserLogsRepoImpl extends UserLogsRepo {
  override def delete(userLogId: /* user-picked */ UUIDCustom)(implicit c: Connection): Boolean = {
    SQL"""delete from public.user_logs where "user_log_id" = ${ParameterValue(
        userLogId,
        null,
        implicitly[ToStatement[UUIDCustom]]
      )}""".executeUpdate() > 0
  }
  override def delete: DeleteBuilder[UserLogsFields, UserLogsRow] = {
    DeleteBuilder("public.user_logs", UserLogsFields)
  }
  override def insert(unsaved: UserLogsRow)(implicit c: Connection): UserLogsRow = {
    SQL"""insert into public.user_logs("user_log_id", "user_id", "message", "created_at")
          values (${ParameterValue(
        unsaved.userLogId,
        null,
        implicitly[ToStatement[UUIDCustom]]
      )}::uuid, ${ParameterValue(unsaved.userId, null, implicitly[ToStatement[UUIDCustom]])}::uuid, ${ParameterValue(
        unsaved.message,
        null,
        ToStatement.stringToStatement
      )}, ${ParameterValue(unsaved.createdAt, null, implicitly[ToStatement[InstantCustom]])}::timestamptz)
          returning "user_log_id", "user_id", "message", "created_at"::text
       """
      .executeInsert(UserLogsRow.rowParser(1).single)

  }
  override def insert(unsaved: UserLogsRowUnsaved)(implicit c: Connection): UserLogsRow = {
    val namedParameters = List(
      Some(
        (
          NamedParameter("user_log_id", ParameterValue(unsaved.userLogId, null, implicitly[ToStatement[UUIDCustom]])),
          "::uuid"
        )
      ),
      Some(
        (NamedParameter("user_id", ParameterValue(unsaved.userId, null, implicitly[ToStatement[UUIDCustom]])), "::uuid")
      ),
      Some((NamedParameter("message", ParameterValue(unsaved.message, null, ToStatement.stringToStatement)), "")),
      unsaved.createdAt match {
        case Defaulted.UseDefault => None
        case Defaulted.Provided(value) =>
          Some(
            (
              NamedParameter("created_at", ParameterValue(value, null, implicitly[ToStatement[InstantCustom]])),
              "::timestamptz"
            )
          )
      }
    ).flatten
    val quote = '"'.toString
    if (namedParameters.isEmpty) {
      SQL"""insert into public.user_logs default values
            returning "user_log_id", "user_id", "message", "created_at"::text
         """
        .executeInsert(UserLogsRow.rowParser(1).single)
    } else {
      val q = s"""insert into public.user_logs(${namedParameters
          .map { case (x, _) => quote + x.name + quote }
          .mkString(", ")})
                  values (${namedParameters.map { case (np, cast) => s"{${np.name}}$cast" }.mkString(", ")})
                  returning "user_log_id", "user_id", "message", "created_at"::text
               """
      SimpleSql(SQL(q), namedParameters.map { case (np, _) => np.tupled }.toMap, RowParser.successful)
        .executeInsert(UserLogsRow.rowParser(1).single)
    }

  }
  override def select: SelectBuilder[UserLogsFields, UserLogsRow] = {
    SelectBuilderSql("public.user_logs", UserLogsFields, UserLogsRow.rowParser)
  }
  override def selectAll(implicit c: Connection): List[UserLogsRow] = {
    SQL"""select "user_log_id", "user_id", "message", "created_at"::text
          from public.user_logs
       """.as(UserLogsRow.rowParser(1).*)
  }
  override def selectById(userLogId: /* user-picked */ UUIDCustom)(implicit c: Connection): Option[UserLogsRow] = {
    SQL"""select "user_log_id", "user_id", "message", "created_at"::text
          from public.user_logs
          where "user_log_id" = ${ParameterValue(userLogId, null, implicitly[ToStatement[UUIDCustom]])}
       """.as(UserLogsRow.rowParser(1).singleOpt)
  }
  override def selectByIds(
      userLogIds: Array[ /* user-picked */ UUIDCustom]
  )(implicit c: Connection, toStatement: ToStatement[Array[ /* user-picked */ UUIDCustom]]): List[UserLogsRow] = {
    SQL"""select "user_log_id", "user_id", "message", "created_at"::text
          from public.user_logs
          where "user_log_id" = ANY(${userLogIds})
       """.as(UserLogsRow.rowParser(1).*)

  }
  override def update(row: UserLogsRow)(implicit c: Connection): Boolean = {
    val userLogId = row.userLogId
    SQL"""update public.user_logs
          set "user_id" = ${ParameterValue(row.userId, null, implicitly[ToStatement[UUIDCustom]])}::uuid,
              "message" = ${ParameterValue(row.message, null, ToStatement.stringToStatement)},
              "created_at" = ${ParameterValue(row.createdAt, null, implicitly[ToStatement[InstantCustom]])}::timestamptz
          where "user_log_id" = ${ParameterValue(userLogId, null, implicitly[ToStatement[UUIDCustom]])}
       """.executeUpdate() > 0
  }
  override def update: UpdateBuilder[UserLogsFields, UserLogsRow] = {
    UpdateBuilder("public.user_logs", UserLogsFields, UserLogsRow.rowParser)
  }
  override def upsert(unsaved: UserLogsRow)(implicit c: Connection): UserLogsRow = {
    SQL"""insert into public.user_logs("user_log_id", "user_id", "message", "created_at")
          values (
            ${ParameterValue(unsaved.userLogId, null, implicitly[ToStatement[UUIDCustom]])}::uuid,
            ${ParameterValue(unsaved.userId, null, implicitly[ToStatement[UUIDCustom]])}::uuid,
            ${ParameterValue(unsaved.message, null, ToStatement.stringToStatement)},
            ${ParameterValue(unsaved.createdAt, null, implicitly[ToStatement[InstantCustom]])}::timestamptz
          )
          on conflict ("user_log_id")
          do update set
            "user_id" = EXCLUDED."user_id",
            "message" = EXCLUDED."message",
            "created_at" = EXCLUDED."created_at"
          returning "user_log_id", "user_id", "message", "created_at"::text
       """
      .executeInsert(UserLogsRow.rowParser(1).single)

  }
}
