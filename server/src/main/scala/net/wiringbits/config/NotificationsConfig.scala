package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class NotificationsConfig(interval: FiniteDuration) {
  override def toString: String = {
    s"NotificationsConfig(interval = $interval)"
  }
}

object NotificationsConfig {
  def apply(config: Configuration): NotificationsConfig = {
    val interval = config.get[FiniteDuration]("interval")
    NotificationsConfig(interval)
  }
}
