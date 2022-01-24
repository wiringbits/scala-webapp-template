package net.wiringbits.models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class AWSAccessKeyId(string: String) extends SecretValue(string)

object AWSAccessKeyId {

  implicit val configLoader: ConfigLoader[AWSAccessKeyId] = (config: Config, path: String) => {
    AWSAccessKeyId(string = config.getString(path))
  }
}
