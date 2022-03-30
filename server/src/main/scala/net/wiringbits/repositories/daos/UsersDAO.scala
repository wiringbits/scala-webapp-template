package net.wiringbits.repositories.daos

import net.wiringbits.common.models.{Email, Name}
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
          ${request.name.string},
          ${request.email.string},
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

  def find(email: Email)(implicit conn: Connection): Option[User] = {
    SQL"""
        SELECT user_id, name, email, password, created_at, verified_on
        FROM users
        WHERE email = ${email.string}::CITEXT
        """.as(userParser.singleOpt)
  }

  def find(userId: UUID)(implicit conn: Connection): Option[User] = {
    SQL"""
        SELECT user_id, name, email, password, created_at, verified_on
        FROM users
        WHERE user_id = ${userId.toString}::UUID
        """.as(userParser.singleOpt)
  }

  def updateName(userId: UUID, name: Name)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE users
      SET name = ${name.string}
      WHERE user_id = ${userId.toString}::UUID
    """.execute()
  }

  def verify(userId: UUID)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE users
      SET verified_on = NOW()
      WHERE user_id = ${userId.toString}::UUID
    """.execute()
  }

  def resetPassword(userId: UUID, password: String)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE users
      SET password = $password
      WHERE user_id = ${userId.toString}::UUID
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
