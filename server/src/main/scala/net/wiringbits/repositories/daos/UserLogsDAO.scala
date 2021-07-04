package net.wiringbits.repositories.daos

import net.wiringbits.repositories.models.UserLog

import java.sql.Connection
import java.util.UUID

object UserLogsDAO {

  import anorm._

  def create(request: UserLog.CreateUserLog)(implicit conn: Connection): Unit = {
    val _ = SQL"""
        INSERT INTO user_logs
          (user_log_id, user_id, message, created_at)
        VALUES (
          ${request.userLogId.toString}::UUID,
          ${request.userId.toString}::UUID,
          ${request.message},
          NOW()
        )
        """
      .execute()
  }

  def logs(userId: UUID)(implicit conn: Connection): List[UserLog] = {
    SQL"""
      SELECT user_log_id, user_id, message, created_at
      FROM user_logs
      WHERE user_id = ${userId.toString}::UUID
      ORDER BY created_at DESC, user_log_id
  """.as(userLogParser.*)
  }
}
