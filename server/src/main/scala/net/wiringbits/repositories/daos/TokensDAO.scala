package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.Token

import java.sql.Connection
import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.FiniteDuration

object TokensDAO {

  def create(request: Token.CreateToken, tokenExp: FiniteDuration)(implicit conn: Connection): Unit = {
    val _ =
      SQL"""
        INSERT INTO tokens
          (token_id, token, token_type, created_at, expires_at, user_id)
        VALUES (
          ${request.id.toString}::UUID,
          ${request.token.toString}::UUID,
          ${request.tokenType.toString}::TEXT,
          ${Instant.now}::TIMESTAMP,
          ${request.expirationHour(tokenExp)}::TIMESTAMP,
          ${request.userId.toString}::UUID
        )
        """
        .execute()
  }

  def find(userId: UUID, token: UUID)(implicit conn: Connection): Option[Token] =
    SQL"""
        SELECT token_id, token, token_type, created_at, expires_at, user_id
        FROM tokens
        WHERE user_id = ${userId.toString}::UUID
          AND token = ${token.toString}::UUID
        """.as(tokenParser.singleOpt)

  def find(userId: UUID)(implicit conn: Connection): List[Token] =
    SQL"""
        SELECT token_id, token, token_type, created_at, expires_at, user_id
        FROM tokens
        WHERE user_id = ${userId.toString}::UUID
        ORDER BY created_at DESC
       """.as(tokenParser.*)

  def delete(token: Token)(implicit conn: Connection): Unit = {
    val _ =
      SQL"""
         DELETE FROM tokens
         WHERE token_id = ${token.id.toString}::UUID
       """
        .executeUpdate()
  }
}
