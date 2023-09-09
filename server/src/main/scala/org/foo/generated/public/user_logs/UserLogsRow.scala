/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package org.foo.generated.public.user_logs

import anorm.Column
import anorm.RowParser
import anorm.Success
import org.foo.generated.customtypes.TypoOffsetDateTime
import org.foo.generated.public.users.UsersId
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import scala.collection.immutable.ListMap
import scala.util.Try

case class UserLogsRow(
  userLogId: UserLogsId,
  /** Points to [[users.UsersRow.userId]] */
  userId: UsersId,
  message: String,
  createdAt: TypoOffsetDateTime
)

object UserLogsRow {
  implicit lazy val reads: Reads[UserLogsRow] = Reads[UserLogsRow](json => JsResult.fromTry(
      Try(
        UserLogsRow(
          userLogId = json.\("user_log_id").as(UserLogsId.reads),
          userId = json.\("user_id").as(UsersId.reads),
          message = json.\("message").as(Reads.StringReads),
          createdAt = json.\("created_at").as(TypoOffsetDateTime.reads)
        )
      )
    ),
  )
  def rowParser(idx: Int): RowParser[UserLogsRow] = RowParser[UserLogsRow] { row =>
    Success(
      UserLogsRow(
        userLogId = row(idx + 0)(UserLogsId.column),
        userId = row(idx + 1)(UsersId.column),
        message = row(idx + 2)(Column.columnToString),
        createdAt = row(idx + 3)(TypoOffsetDateTime.column)
      )
    )
  }
  implicit lazy val writes: OWrites[UserLogsRow] = OWrites[UserLogsRow](o =>
    new JsObject(ListMap[String, JsValue](
      "user_log_id" -> UserLogsId.writes.writes(o.userLogId),
      "user_id" -> UsersId.writes.writes(o.userId),
      "message" -> Writes.StringWrites.writes(o.message),
      "created_at" -> TypoOffsetDateTime.writes.writes(o.createdAt)
    ))
  )
}
