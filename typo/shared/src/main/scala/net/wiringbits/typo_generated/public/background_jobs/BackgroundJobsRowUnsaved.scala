/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package net
package wiringbits
package typo_generated
package public
package background_jobs

import net.wiringbits.typo_generated.customtypes.Defaulted
import net.wiringbits.typo_generated.customtypes.TypoJsonb
import net.wiringbits.typo_generated.customtypes.TypoOffsetDateTime
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import scala.collection.immutable.ListMap
import scala.util.Try

/** This class corresponds to a row in table `public.background_jobs` which has not been persisted yet */
case class BackgroundJobsRowUnsaved(
  backgroundJobId: BackgroundJobsId,
  `type`: String,
  payload: TypoJsonb,
  status: String,
  statusDetails: Option[String],
  /** Default: 0 */
  errorCount: Defaulted[Option[Int]] = Defaulted.UseDefault,
  /** Default: now() */
  executeAt: Defaulted[TypoOffsetDateTime] = Defaulted.UseDefault,
  /** Default: now() */
  createdAt: Defaulted[TypoOffsetDateTime] = Defaulted.UseDefault,
  /** Default: now() */
  updatedAt: Defaulted[TypoOffsetDateTime] = Defaulted.UseDefault
) {
  def toRow(errorCountDefault: => Option[Int], executeAtDefault: => TypoOffsetDateTime, createdAtDefault: => TypoOffsetDateTime, updatedAtDefault: => TypoOffsetDateTime): BackgroundJobsRow =
    BackgroundJobsRow(
      backgroundJobId = backgroundJobId,
      `type` = `type`,
      payload = payload,
      status = status,
      statusDetails = statusDetails,
      errorCount = errorCount match {
                     case Defaulted.UseDefault => errorCountDefault
                     case Defaulted.Provided(value) => value
                   },
      executeAt = executeAt match {
                    case Defaulted.UseDefault => executeAtDefault
                    case Defaulted.Provided(value) => value
                  },
      createdAt = createdAt match {
                    case Defaulted.UseDefault => createdAtDefault
                    case Defaulted.Provided(value) => value
                  },
      updatedAt = updatedAt match {
                    case Defaulted.UseDefault => updatedAtDefault
                    case Defaulted.Provided(value) => value
                  }
    )
}
object BackgroundJobsRowUnsaved {
  implicit lazy val reads: Reads[BackgroundJobsRowUnsaved] = Reads[BackgroundJobsRowUnsaved](json => JsResult.fromTry(
      Try(
        BackgroundJobsRowUnsaved(
          backgroundJobId = json.\("background_job_id").as(BackgroundJobsId.reads),
          `type` = json.\("type").as(Reads.StringReads),
          payload = json.\("payload").as(TypoJsonb.reads),
          status = json.\("status").as(Reads.StringReads),
          statusDetails = json.\("status_details").toOption.map(_.as(Reads.StringReads)),
          errorCount = json.\("error_count").as(Defaulted.readsOpt(Reads.IntReads)),
          executeAt = json.\("execute_at").as(Defaulted.reads(TypoOffsetDateTime.reads)),
          createdAt = json.\("created_at").as(Defaulted.reads(TypoOffsetDateTime.reads)),
          updatedAt = json.\("updated_at").as(Defaulted.reads(TypoOffsetDateTime.reads))
        )
      )
    ),
  )
  implicit lazy val writes: OWrites[BackgroundJobsRowUnsaved] = OWrites[BackgroundJobsRowUnsaved](o =>
    new JsObject(ListMap[String, JsValue](
      "background_job_id" -> BackgroundJobsId.writes.writes(o.backgroundJobId),
      "type" -> Writes.StringWrites.writes(o.`type`),
      "payload" -> TypoJsonb.writes.writes(o.payload),
      "status" -> Writes.StringWrites.writes(o.status),
      "status_details" -> Writes.OptionWrites(Writes.StringWrites).writes(o.statusDetails),
      "error_count" -> Defaulted.writes(Writes.OptionWrites(Writes.IntWrites)).writes(o.errorCount),
      "execute_at" -> Defaulted.writes(TypoOffsetDateTime.writes).writes(o.executeAt),
      "created_at" -> Defaulted.writes(TypoOffsetDateTime.writes).writes(o.createdAt),
      "updated_at" -> Defaulted.writes(TypoOffsetDateTime.writes).writes(o.updatedAt)
    ))
  )
}