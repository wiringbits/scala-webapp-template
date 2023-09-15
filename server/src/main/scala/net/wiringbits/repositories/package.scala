package net.wiringbits

import anorm.{Macro, RowParser}
import net.wiringbits.typo_generated.public.background_jobs.BackgroundJobsRow

package object repositories {
  // Typo parser doesn't work for akka streams, we have to define our own
  implicit val backgroundJobParser: RowParser[BackgroundJobsRow] = {
    Macro.parser[BackgroundJobsRow](
      "background_job_id",
      "type",
      "payload",
      "status",
      "status_details",
      "error_count",
      "execute_at",
      "created_at",
      "updated_at"
    )
  }
}
