package net.wiringbits.config

import net.wiringbits.models.SecretAccessKey
import play.api.Configuration

case class AWSConfig(accessKeyId: AccessKeyId, secretAccessKey: SecretAccessKey, region: String)

object AWSConfig {
  def apply(config: Configuration): AWSConfig = {
    val accessKeyId = config.get[String]("accessKeyId")
    val secretAccessKey = config.get[String]("secretAccessKey")
    val region = config.get[String]("region")

    AWSConfig(AccessKeyId(accessKeyId), SecretAccessKey(secretAccessKey), region)
  }
}
