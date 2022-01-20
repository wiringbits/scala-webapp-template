package net.wiringbits.config

import play.api.Configuration

case class AWSRegionConfig(region: String)

object AWSRegionConfig {
  def apply(config: Configuration): AWSRegionConfig = {
    val region = config.get[String]("region")
    AWSRegionConfig(region)
  }
}
