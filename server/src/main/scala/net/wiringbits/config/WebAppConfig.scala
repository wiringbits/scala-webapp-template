package net.wiringbits.config

import play.api.Configuration

case class WebAppConfig(host: String) {
  override def toString: String = {
    s"WebAppConfig(host = $host)"
  }
}

object WebAppConfig {
  def apply(config: Configuration): WebAppConfig = {
    val url = config.get[String]("host")

    WebAppConfig(url)
  }
}
