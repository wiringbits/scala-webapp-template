/** File has been automatically generated by `typo`.
  *
  * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
  */
package net
package wiringbits
package typo_generated
package public
package background_jobs

import net.wiringbits.common.models.InstantCustom
import net.wiringbits.common.models.UUIDCustom
import net.wiringbits.common.models.enums.BackgroundJobStatus
import net.wiringbits.common.models.enums.BackgroundJobType
import net.wiringbits.typo_generated.customtypes.TypoJsonb
import typo.dsl.SqlExpr.Field
import typo.dsl.SqlExpr.IdField
import typo.dsl.SqlExpr.OptField

trait BackgroundJobsFields[Row] {
  val backgroundJobId: IdField[ /* user-picked */ UUIDCustom, Row]
  val `type`: Field[ /* user-picked */ BackgroundJobType, Row]
  val payload: Field[TypoJsonb, Row]
  val status: Field[ /* user-picked */ BackgroundJobStatus, Row]
  val statusDetails: OptField[String, Row]
  val errorCount: OptField[Int, Row]
  val executeAt: Field[ /* user-picked */ InstantCustom, Row]
  val createdAt: Field[ /* user-picked */ InstantCustom, Row]
  val updatedAt: Field[ /* user-picked */ InstantCustom, Row]
}
object BackgroundJobsFields extends BackgroundJobsStructure[BackgroundJobsRow](None, identity, (_, x) => x)
