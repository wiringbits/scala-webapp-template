/** File has been automatically generated by `typo`.
  *
  * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
  */
package net
package wiringbits
package typo_generated
package public
package user_logs

import java.sql.Connection
import typo.dsl.DeleteBuilder
import typo.dsl.SelectBuilder
import typo.dsl.UpdateBuilder

trait UserLogsRepo {
  def delete(userLogId: UserLogsId)(implicit c: Connection): Boolean
  def delete: DeleteBuilder[UserLogsFields, UserLogsRow]
  def insert(unsaved: UserLogsRow)(implicit c: Connection): UserLogsRow
  def insert(unsaved: UserLogsRowUnsaved)(implicit c: Connection): UserLogsRow
  def select: SelectBuilder[UserLogsFields, UserLogsRow]
  def selectAll(implicit c: Connection): List[UserLogsRow]
  def selectById(userLogId: UserLogsId)(implicit c: Connection): Option[UserLogsRow]
  def selectByIds(userLogIds: Array[UserLogsId])(implicit c: Connection): List[UserLogsRow]
  def update(row: UserLogsRow)(implicit c: Connection): Boolean
  def update: UpdateBuilder[UserLogsFields, UserLogsRow]
  def upsert(unsaved: UserLogsRow)(implicit c: Connection): UserLogsRow
}
