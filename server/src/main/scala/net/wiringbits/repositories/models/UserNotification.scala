package net.wiringbits.repositories.models

import java.time.Instant
import java.util.UUID

case class UserNotification(
    id: UUID,
    userId: UUID,
    notificationType: NotificationType,
    subject: String,
    message: String,
    status: NotificationStatus,
    statusDetails: Option[String],
    errorCount: Int,
    executeAt: Instant,
    createdAt: Instant,
    updatedAt: Instant
)

object UserNotification {
  case class Create(
      id: UUID,
      userId: UUID,
      notificationType: NotificationType,
      subject: String,
      message: String,
      status: NotificationStatus,
      executeAt: Instant,
      createdAt: Instant,
      updatedAt: Instant
  )
}
