package net.wiringbits.repositories.models

import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import play.api.libs.json.JsValue

import java.time.Instant
import java.util.UUID

case class BackgroundJobData(
    id: UUID,
    `type`: BackgroundJobType,
    payload: JsValue,
    status: BackgroundJobStatus,
    statusDetails: Option[String],
    errorCount: Int,
    executeAt: Instant,
    createdAt: Instant,
    updatedAt: Instant
)

object BackgroundJobData {
  case class Create(
      id: UUID,
      `type`: BackgroundJobType,
      payload: BackgroundJobPayload,
      status: BackgroundJobStatus,
      executeAt: Instant,
      createdAt: Instant,
      updatedAt: Instant
  )
}
