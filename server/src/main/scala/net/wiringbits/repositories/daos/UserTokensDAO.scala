package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.UserToken

import java.sql.Connection
import java.util.UUID

object UserTokensDAO {

  def create(request: UserToken.Create)(implicit
      conn: Connection
  ): Unit = {
    val _ = SQL"""
        INSERT INTO user_tokens
          (user_token_id, token, token_type, created_at, expires_at, user_id)
        VALUES (
          ${request.id.toString}::UUID,
          ${request.token}::TEXT,
          ${request.tokenType.toString}::TEXT,
          ${request.createdAt}::TIMESTAMPTZ,
          ${request.expiresAt}::TIMESTAMPTZ,
          ${request.userId.toString}::UUID
        )
        """
      .execute()
  }

  def find(userId: UUID, token: String)(implicit conn: Connection): Option[UserToken] = {
    SQL"""
        SELECT user_token_id, token, token_type, created_at, expires_at, user_id
        FROM user_tokens
        WHERE user_id = ${userId.toString}::UUID
          AND token = $token::TEXT
        """.as(tokenParser.singleOpt)
  }

  def find(userId: UUID)(implicit conn: Connection): List[UserToken] = {
    SQL"""
        SELECT user_token_id, token, token_type, created_at, expires_at, user_id
        FROM user_tokens
        WHERE user_id = ${userId.toString}::UUID
        ORDER BY created_at DESC, user_token_id
       """.as(tokenParser.*)
  }

  def delete(tokenId: UUID, userId: UUID)(implicit conn: Connection): Unit = {
    val _ = SQL"""
        DELETE FROM user_tokens
        WHERE user_id = ${userId.toString}::UUID
         AND user_token_id = ${tokenId.toString}::UUID
       """
      .executeUpdate()
  }
}
