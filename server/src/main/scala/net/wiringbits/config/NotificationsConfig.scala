package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class NotificationsConfig(delayedInit: FiniteDuration, interval: FiniteDuration, delayIfFailure: FiniteDuration) {
  override def toString: String = {
    s"NotificationsConfig(delayedInit = $delayedInit, interval = $interval, delayIfFailure = $delayIfFailure)"
  }
}

object NotificationsConfig {
  def apply(config: Configuration): NotificationsConfig = {
    val delayedInit = config.get[FiniteDuration]("delayedInit")
    val interval = config.get[FiniteDuration]("interval")
    val processAfter = config.get[FiniteDuration]("delayIfFailure")
    NotificationsConfig(delayedInit, interval, processAfter)
  }
}
