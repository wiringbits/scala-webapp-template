package net.wiringbits.config

import play.api.Configuration

case class FrontendConfig(host: String)

object FrontendConfig {
  def apply(config: Configuration): FrontendConfig = {
    val url = config.get[String]("host")

    FrontendConfig(url)
  }
}
