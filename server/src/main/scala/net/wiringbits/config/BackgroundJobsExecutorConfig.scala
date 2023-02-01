package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class BackgroundJobsExecutorConfig(interval: FiniteDuration) {
  override def toString: String = {
    s"BackgroundJobsExecutorConfig(interval = $interval)"
  }
}

object BackgroundJobsExecutorConfig {
  def apply(config: Configuration): BackgroundJobsExecutorConfig = {
    val interval = config.get[FiniteDuration]("interval")
    BackgroundJobsExecutorConfig(interval)
  }
}
