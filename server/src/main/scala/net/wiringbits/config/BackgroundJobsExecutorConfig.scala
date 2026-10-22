package net.wiringbits.config

import net.wiringbits.config.BackgroundJobsExecutorConfig.TypeSettings
import net.wiringbits.models.jobs.BackgroundJobType
import play.api.Configuration

import scala.concurrent.duration.*

case class BackgroundJobsExecutorConfig(interval: FiniteDuration, perType: Map[BackgroundJobType, TypeSettings]) {
  private val missingTypes = BackgroundJobType.values.filterNot(perType.contains)
  if (missingTypes.nonEmpty) {
    throw new IllegalStateException(
      s"Missing background job settings for types: ${missingTypes.mkString(", ")}"
    )
  }

  private val invalidParallelism = BackgroundJobType.values.filter(t => perType(t).parallelism < 1)
  if (invalidParallelism.nonEmpty) {
    throw new IllegalStateException(
      s"Background job parallelism must be >= 1, invalid types: ${invalidParallelism.mkString(", ")}"
    )
  }

  def settingsFor(jobType: BackgroundJobType): TypeSettings = perType(jobType)

  override def toString: String = {
    val perTypeString = BackgroundJobType.values.map(t => s"$t = ${perType(t)}").mkString(", ")
    s"BackgroundJobsExecutorConfig(interval = $interval, perType = [$perTypeString])"
  }
}

object BackgroundJobsExecutorConfig {
  case class TypeSettings(parallelism: Int, timeout: FiniteDuration, pollInterval: FiniteDuration)

  // One entry per BackgroundJobType: how many jobs of that type run at once, how long a handler may take before the
  // job is recorded as failed, and how long to wait after an empty claim before polling again. Tune per type.
  val defaultPerType: Map[BackgroundJobType, TypeSettings] = Map(
    BackgroundJobType.SendEmail -> TypeSettings(parallelism = 10, timeout = 1.minute, pollInterval = 5.seconds)
  )

  def apply(config: Configuration): BackgroundJobsExecutorConfig = {
    val interval = config.get[FiniteDuration]("interval")
    BackgroundJobsExecutorConfig(interval, defaultPerType)
  }
}
