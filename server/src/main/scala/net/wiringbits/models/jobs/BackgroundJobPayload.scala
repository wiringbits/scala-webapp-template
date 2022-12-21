package net.wiringbits.models.jobs

import net.wiringbits.common.models.Email
import play.api.libs.json.{Format, Json, Writes}

sealed trait BackgroundJobPayload extends Product with Serializable

/** NOTE: Updating these models can cause tasks to fail, for example, adding an extra argument to SendEmail would cause
  * the json parsing to fail when we already have jobs in the database
  */
object BackgroundJobPayload {
  case class SendEmail(email: Email, subject: String, body: String) extends BackgroundJobPayload

  object SendEmail {
    implicit val sendEmailFormat: Format[SendEmail] = Json.format
  }

  implicit val backgroundJobPayloadWrites: Writes[BackgroundJobPayload] = { case payload: SendEmail =>
    Json.toJson(payload)
  }
}
