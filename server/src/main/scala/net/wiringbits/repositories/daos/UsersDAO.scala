package net.wiringbits.repositories.daos

import net.wiringbits.repositories.models.User

import java.sql.Connection
import java.util.UUID

object UsersDAO {

  import anorm._

  def create(request: User.CreateUser)(implicit conn: Connection): Unit = {
    val _ = SQL"""
        INSERT INTO users
          (user_id, name, email, password, created_at)
        VALUES (
          ${request.id.toString}::UUID,
          ${request.name},
          ${request.email},
          ${request.hashedPassword},
          NOW()
        )
        """
      .execute()
  }

  def all()(implicit conn: Connection): List[User] = {
    SQL"""
        SELECT user_id, name, email, password, created_at, verified_on
        FROM users
        """.as(userParser.*)
  }

  def find(email: String)(implicit conn: Connection): Option[User] = {
    SQL"""
        SELECT user_id, name, email, password, created_at, verified_on
        FROM users
        WHERE email = $email
        """.as(userParser.singleOpt)
  }

  def find(userId: UUID)(implicit conn: Connection): Option[User] = {
    SQL"""
        SELECT user_id, name, email, password, created_at, verified_on
        FROM users
        WHERE user_id = ${userId.toString}::UUID
        """.as(userParser.singleOpt)
  }

  def updateName(userId: UUID, name: String)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE users
      SET name = $name
      WHERE user_id = ${userId.toString}::UUID
    """.execute()
  }

  def verify(userId: UUID, token: UUID)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE users
      SET verified_on = NOW()
      FROM tokens
      WHERE users.user_id = tokens.user_id
      AND tokens.user_id = ${userId.toString}::UUID
      AND tokens.token = ${token.toString}::UUID
    """.execute()
  }

  def findUserForUpdate(userId: UUID)(implicit conn: Connection): Option[User] = {
    SQL"""
        SELECT user_id, name, email, password, created_at, verified_on
        FROM users
        WHERE user_id = ${userId.toString}::UUID
        FOR UPDATE NOWAIT
        """.as(userParser.singleOpt)
  }
}
