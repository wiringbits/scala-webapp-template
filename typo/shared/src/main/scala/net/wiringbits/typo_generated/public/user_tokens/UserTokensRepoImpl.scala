/** File has been automatically generated by `typo`.
  *
  * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
  */
package net
package wiringbits
package typo_generated
package public
package user_tokens

import anorm.ParameterValue
import anorm.SqlStringInterpolation
import anorm.ToStatement
import java.sql.Connection
import net.wiringbits.typo_generated.customtypes.TypoOffsetDateTime
import net.wiringbits.typo_generated.public.users.UsersId
import typo.dsl.DeleteBuilder
import typo.dsl.SelectBuilder
import typo.dsl.SelectBuilderSql
import typo.dsl.UpdateBuilder

object UserTokensRepoImpl extends UserTokensRepo {
  override def delete(userTokenId: UserTokensId)(implicit c: Connection): Boolean = {
    SQL"""delete from public.user_tokens where "user_token_id" = ${ParameterValue(
        userTokenId,
        null,
        UserTokensId.toStatement
      )}""".executeUpdate() > 0
  }
  override def delete: DeleteBuilder[UserTokensFields, UserTokensRow] = {
    DeleteBuilder("public.user_tokens", UserTokensFields)
  }
  override def insert(unsaved: UserTokensRow)(implicit c: Connection): UserTokensRow = {
    SQL"""insert into public.user_tokens("user_token_id", "token", "token_type", "created_at", "expires_at", "user_id")
          values (${ParameterValue(unsaved.userTokenId, null, UserTokensId.toStatement)}::uuid, ${ParameterValue(
        unsaved.token,
        null,
        ToStatement.stringToStatement
      )}, ${ParameterValue(unsaved.tokenType, null, ToStatement.stringToStatement)}, ${ParameterValue(
        unsaved.createdAt,
        null,
        TypoOffsetDateTime.toStatement
      )}::timestamptz, ${ParameterValue(
        unsaved.expiresAt,
        null,
        TypoOffsetDateTime.toStatement
      )}::timestamptz, ${ParameterValue(unsaved.userId, null, UsersId.toStatement)}::uuid)
          returning "user_token_id", "token", "token_type", "created_at"::text, "expires_at"::text, "user_id"
       """
      .executeInsert(UserTokensRow.rowParser(1).single)

  }
  override def select: SelectBuilder[UserTokensFields, UserTokensRow] = {
    SelectBuilderSql("public.user_tokens", UserTokensFields, UserTokensRow.rowParser)
  }
  override def selectAll(implicit c: Connection): List[UserTokensRow] = {
    SQL"""select "user_token_id", "token", "token_type", "created_at"::text, "expires_at"::text, "user_id"
          from public.user_tokens
       """.as(UserTokensRow.rowParser(1).*)
  }
  override def selectById(userTokenId: UserTokensId)(implicit c: Connection): Option[UserTokensRow] = {
    SQL"""select "user_token_id", "token", "token_type", "created_at"::text, "expires_at"::text, "user_id"
          from public.user_tokens
          where "user_token_id" = ${ParameterValue(userTokenId, null, UserTokensId.toStatement)}
       """.as(UserTokensRow.rowParser(1).singleOpt)
  }
  override def selectByIds(userTokenIds: Array[UserTokensId])(implicit c: Connection): List[UserTokensRow] = {
    SQL"""select "user_token_id", "token", "token_type", "created_at"::text, "expires_at"::text, "user_id"
          from public.user_tokens
          where "user_token_id" = ANY(${userTokenIds})
       """.as(UserTokensRow.rowParser(1).*)

  }
  override def update(row: UserTokensRow)(implicit c: Connection): Boolean = {
    val userTokenId = row.userTokenId
    SQL"""update public.user_tokens
          set "token" = ${ParameterValue(row.token, null, ToStatement.stringToStatement)},
              "token_type" = ${ParameterValue(row.tokenType, null, ToStatement.stringToStatement)},
              "created_at" = ${ParameterValue(row.createdAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
              "expires_at" = ${ParameterValue(row.expiresAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
              "user_id" = ${ParameterValue(row.userId, null, UsersId.toStatement)}::uuid
          where "user_token_id" = ${ParameterValue(userTokenId, null, UserTokensId.toStatement)}
       """.executeUpdate() > 0
  }
  override def update: UpdateBuilder[UserTokensFields, UserTokensRow] = {
    UpdateBuilder("public.user_tokens", UserTokensFields, UserTokensRow.rowParser)
  }
  override def upsert(unsaved: UserTokensRow)(implicit c: Connection): UserTokensRow = {
    SQL"""insert into public.user_tokens("user_token_id", "token", "token_type", "created_at", "expires_at", "user_id")
          values (
            ${ParameterValue(unsaved.userTokenId, null, UserTokensId.toStatement)}::uuid,
            ${ParameterValue(unsaved.token, null, ToStatement.stringToStatement)},
            ${ParameterValue(unsaved.tokenType, null, ToStatement.stringToStatement)},
            ${ParameterValue(unsaved.createdAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
            ${ParameterValue(unsaved.expiresAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
            ${ParameterValue(unsaved.userId, null, UsersId.toStatement)}::uuid
          )
          on conflict ("user_token_id")
          do update set
            "token" = EXCLUDED."token",
            "token_type" = EXCLUDED."token_type",
            "created_at" = EXCLUDED."created_at",
            "expires_at" = EXCLUDED."expires_at",
            "user_id" = EXCLUDED."user_id"
          returning "user_token_id", "token", "token_type", "created_at"::text, "expires_at"::text, "user_id"
       """
      .executeInsert(UserTokensRow.rowParser(1).single)

  }
}
