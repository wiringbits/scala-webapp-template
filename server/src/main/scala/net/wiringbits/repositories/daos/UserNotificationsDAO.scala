package net.wiringbits.repositories.daos

import net.wiringbits.repositories.models.{NotificationStatus, UserNotification}

import java.sql.Connection
import java.time.{Clock, Instant}
import java.util.UUID

object UserNotificationsDAO {

  import anorm._

  def create(request: UserNotification.Create)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      INSERT INTO user_notifications
        (user_notification_id, user_id, notification_type, subject, message, status, execute_at, created_at, updated_at)
      VALUES (
        ${request.id.toString}::UUID,
        ${request.userId.toString}::UUID,
        ${request.notificationType.toString}::TEXT,
        ${request.subject},
        ${request.message},
        ${request.status.toString}::TEXT,
        ${request.executeAt}::TIMESTAMPTZ,
        ${request.createdAt}::TIMESTAMPTZ,
        ${request.updatedAt}::TIMESTAMPTZ,
      )
      """
      .execute()
  }

  def getPendingNotifications()(implicit conn: Connection, clock: Clock): List[UserNotification] = {
    SQL"""
      SELECT user_notification_id, user_id, notification_type, subject, message, status, status_details, error_count, execute_at, created_at, updated_at
      FROM user_notifications
      WHERE status != ${NotificationStatus.Success.toString}
        AND execute_at <= ${clock.instant()}
        AND error_count < 10 
      ORDER BY created_at, user_notification_id
      """.as(userNotificationParser.*)
  }

  def setStatusToFailed(notificationId: UUID, executeAt: Instant, failReason: String)(implicit
      conn: Connection
  ): Unit = {
    val _ = SQL"""
      UPDATE user_notifications SET 
        status = ${NotificationStatus.Failed.toString}::TEXT,
        status_details = $failReason,
        error_count = error_count + 1,
        execute_at = $executeAt::TIMESTAMPTZ,
        updated_at = ${Instant.now()}
      WHERE user_notification_id = ${notificationId.toString}::UUID
      """
      .execute()
  }

  def setStatusToSuccess(notificationId: UUID)(implicit
      conn: Connection
  ): Unit = {
    val _ = SQL"""
      UPDATE user_notifications SET 
        status = ${NotificationStatus.Success.toString}::TEXT,
        updated_at = ${Instant.now()}
      WHERE user_notification_id = ${notificationId.toString}::UUID
      """
      .execute()
  }
}
