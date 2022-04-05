package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class ExpiredTokensConfig(interval: FiniteDuration) {
  override def toString: String = {
    s"ExpiredTokensConfig(interval = $interval)"
  }
}

object ExpiredTokensConfig {
  def apply(config: Configuration): ExpiredTokensConfig = {
    val interval = config.get[FiniteDuration]("interval")
    ExpiredTokensConfig(interval)
  }
}
