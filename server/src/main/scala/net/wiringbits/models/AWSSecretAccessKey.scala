package net.wiringbits.models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class AWSSecretAccessKey(string: String) extends SecretValue(string)

object AWSSecretAccessKey {

  implicit val configLoader: ConfigLoader[AWSSecretAccessKey] = (config: Config, path: String) => {
    AWSSecretAccessKey(string = config.getString(path))
  }
}
