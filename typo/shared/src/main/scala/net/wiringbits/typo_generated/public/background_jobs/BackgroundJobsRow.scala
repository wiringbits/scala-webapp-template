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

import anorm.Column
import anorm.RowParser
import anorm.Success
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

case class BackgroundJobsRow(
  backgroundJobId: BackgroundJobsId,
  `type`: String,
  payload: TypoJsonb,
  status: String,
  statusDetails: Option[String],
  errorCount: Option[Int],
  executeAt: TypoOffsetDateTime,
  createdAt: TypoOffsetDateTime,
  updatedAt: TypoOffsetDateTime
)

object BackgroundJobsRow {
  implicit lazy val reads: Reads[BackgroundJobsRow] = Reads[BackgroundJobsRow](json => JsResult.fromTry(
      Try(
        BackgroundJobsRow(
          backgroundJobId = json.\("background_job_id").as(BackgroundJobsId.reads),
          `type` = json.\("type").as(Reads.StringReads),
          payload = json.\("payload").as(TypoJsonb.reads),
          status = json.\("status").as(Reads.StringReads),
          statusDetails = json.\("status_details").toOption.map(_.as(Reads.StringReads)),
          errorCount = json.\("error_count").toOption.map(_.as(Reads.IntReads)),
          executeAt = json.\("execute_at").as(TypoOffsetDateTime.reads),
          createdAt = json.\("created_at").as(TypoOffsetDateTime.reads),
          updatedAt = json.\("updated_at").as(TypoOffsetDateTime.reads)
        )
      )
    ),
  )
  def rowParser(idx: Int): RowParser[BackgroundJobsRow] = RowParser[BackgroundJobsRow] { row =>
    Success(
      BackgroundJobsRow(
        backgroundJobId = row(idx + 0)(BackgroundJobsId.column),
        `type` = row(idx + 1)(Column.columnToString),
        payload = row(idx + 2)(TypoJsonb.column),
        status = row(idx + 3)(Column.columnToString),
        statusDetails = row(idx + 4)(Column.columnToOption(Column.columnToString)),
        errorCount = row(idx + 5)(Column.columnToOption(Column.columnToInt)),
        executeAt = row(idx + 6)(TypoOffsetDateTime.column),
        createdAt = row(idx + 7)(TypoOffsetDateTime.column),
        updatedAt = row(idx + 8)(TypoOffsetDateTime.column)
      )
    )
  }
  implicit lazy val writes: OWrites[BackgroundJobsRow] = OWrites[BackgroundJobsRow](o =>
    new JsObject(ListMap[String, JsValue](
      "background_job_id" -> BackgroundJobsId.writes.writes(o.backgroundJobId),
      "type" -> Writes.StringWrites.writes(o.`type`),
      "payload" -> TypoJsonb.writes.writes(o.payload),
      "status" -> Writes.StringWrites.writes(o.status),
      "status_details" -> Writes.OptionWrites(Writes.StringWrites).writes(o.statusDetails),
      "error_count" -> Writes.OptionWrites(Writes.IntWrites).writes(o.errorCount),
      "execute_at" -> TypoOffsetDateTime.writes.writes(o.executeAt),
      "created_at" -> TypoOffsetDateTime.writes.writes(o.createdAt),
      "updated_at" -> TypoOffsetDateTime.writes.writes(o.updatedAt)
    ))
  )
}