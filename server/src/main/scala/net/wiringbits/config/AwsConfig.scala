package net.wiringbits.config

import play.api.Configuration

case class AwsConfig(senderEmail: String)

object AwsConfig {
  def apply(config: Configuration): AwsConfig = {
    val senderEmail = config.get[String]("senderEmail")
    AwsConfig(senderEmail)
  }
}
