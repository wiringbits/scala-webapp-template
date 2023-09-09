/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package org.foo.generated.public.users

import java.sql.Connection
import org.foo.generated.customtypes.TypoUnknownCitext
import typo.dsl.DeleteBuilder
import typo.dsl.SelectBuilder
import typo.dsl.UpdateBuilder

trait UsersRepo {
  def delete(userId: UsersId)(implicit c: Connection): Boolean
  def delete: DeleteBuilder[UsersFields, UsersRow]
  def insert(unsaved: UsersRow)(implicit c: Connection): UsersRow
  def insert(unsaved: UsersRowUnsaved)(implicit c: Connection): UsersRow
  def select: SelectBuilder[UsersFields, UsersRow]
  def selectAll(implicit c: Connection): List[UsersRow]
  def selectById(userId: UsersId)(implicit c: Connection): Option[UsersRow]
  def selectByIds(userIds: Array[UsersId])(implicit c: Connection): List[UsersRow]
  def selectByUnique(email: TypoUnknownCitext)(implicit c: Connection): Option[UsersRow]
  def update(row: UsersRow)(implicit c: Connection): Boolean
  def update: UpdateBuilder[UsersFields, UsersRow]
  def upsert(unsaved: UsersRow)(implicit c: Connection): UsersRow
}
