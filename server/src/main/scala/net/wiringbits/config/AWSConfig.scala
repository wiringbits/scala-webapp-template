package net.wiringbits.config

import com.amazonaws.regions.Regions
import net.wiringbits.models.{AWSAccessKeyId, AWSSecretAccessKey}
import play.api.Configuration

case class AWSConfig(accessKeyId: AWSAccessKeyId, secretAccessKey: AWSSecretAccessKey, region: Regions)

object AWSConfig {
  def apply(config: Configuration): AWSConfig = {
    val accessKeyId = config.get[String]("accessKeyId")
    val secretAccessKey = config.get[String]("secretAccessKey")
    val region = config.get[String]("region")

    AWSConfig(AWSAccessKeyId(accessKeyId), AWSSecretAccessKey(secretAccessKey), Regions.fromName(region))
  }
}
