package net.wiringbits.config

import play.api.Configuration
import software.amazon.awssdk.regions.Region

case class AWSConfig(accessKeyId: String, secretAccessKey: String, region: Region) {
  override def toString: String = {
    import net.wiringbits.util.StringUtils.Implicits._

    s"AwsConfig(region = $region, accessKeyId = ${accessKeyId.mask()}, secretAccessKey = ${secretAccessKey.mask()})"
  }
}

object AWSConfig {
  def apply(config: Configuration): AWSConfig = {
    val accessKeyId = config.get[String]("accessKeyId")
    val secretAccessKey = config.get[String]("secretAccessKey")
    val region = config.get[String]("region")

    AWSConfig(accessKeyId, secretAccessKey, Region.of(region))
  }
}
