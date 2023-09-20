/** File has been automatically generated by `typo`.
  *
  * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
  */
package net
package wiringbits
package typo_generated
package public
package background_jobs

import anorm.ToStatement
import java.sql.Connection
import net.wiringbits.common.models.id.BackgroundJobId
import typo.dsl.DeleteBuilder
import typo.dsl.DeleteBuilder.DeleteBuilderMock
import typo.dsl.DeleteParams
import typo.dsl.SelectBuilder
import typo.dsl.SelectBuilderMock
import typo.dsl.SelectParams
import typo.dsl.UpdateBuilder
import typo.dsl.UpdateBuilder.UpdateBuilderMock
import typo.dsl.UpdateParams

class BackgroundJobsRepoMock(
    toRow: Function1[BackgroundJobsRowUnsaved, BackgroundJobsRow],
    map: scala.collection.mutable.Map[ /* user-picked */ BackgroundJobId, BackgroundJobsRow] =
      scala.collection.mutable.Map.empty
) extends BackgroundJobsRepo {
  override def delete(backgroundJobId: /* user-picked */ BackgroundJobId)(implicit c: Connection): Boolean = {
    map.remove(backgroundJobId).isDefined
  }
  override def delete: DeleteBuilder[BackgroundJobsFields, BackgroundJobsRow] = {
    DeleteBuilderMock(DeleteParams.empty, BackgroundJobsFields, map)
  }
  override def insert(unsaved: BackgroundJobsRow)(implicit c: Connection): BackgroundJobsRow = {
    if (map.contains(unsaved.backgroundJobId))
      sys.error(s"id ${unsaved.backgroundJobId} already exists")
    else
      map.put(unsaved.backgroundJobId, unsaved)
    unsaved
  }
  override def insert(unsaved: BackgroundJobsRowUnsaved)(implicit c: Connection): BackgroundJobsRow = {
    insert(toRow(unsaved))
  }
  override def select: SelectBuilder[BackgroundJobsFields, BackgroundJobsRow] = {
    SelectBuilderMock(BackgroundJobsFields, () => map.values.toList, SelectParams.empty)
  }
  override def selectAll(implicit c: Connection): List[BackgroundJobsRow] = {
    map.values.toList
  }
  override def selectById(
      backgroundJobId: /* user-picked */ BackgroundJobId
  )(implicit c: Connection): Option[BackgroundJobsRow] = {
    map.get(backgroundJobId)
  }
  override def selectByIds(backgroundJobIds: Array[ /* user-picked */ BackgroundJobId])(implicit
      c: Connection,
      toStatement: ToStatement[Array[ /* user-picked */ BackgroundJobId]]
  ): List[BackgroundJobsRow] = {
    backgroundJobIds.flatMap(map.get).toList
  }
  override def update(row: BackgroundJobsRow)(implicit c: Connection): Boolean = {
    map.get(row.backgroundJobId) match {
      case Some(`row`) => false
      case Some(_) =>
        map.put(row.backgroundJobId, row)
        true
      case None => false
    }
  }
  override def update: UpdateBuilder[BackgroundJobsFields, BackgroundJobsRow] = {
    UpdateBuilderMock(UpdateParams.empty, BackgroundJobsFields, map)
  }
  override def upsert(unsaved: BackgroundJobsRow)(implicit c: Connection): BackgroundJobsRow = {
    map.put(unsaved.backgroundJobId, unsaved)
    unsaved
  }
}
