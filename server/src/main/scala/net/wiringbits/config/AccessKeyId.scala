package net.wiringbits.config

import com.typesafe.config.Config
import net.wiringbits.models.SecretValue
import play.api.ConfigLoader

case class AccessKeyId(string: String) extends SecretValue(string)

object AccessKeyId {

  implicit val configLoader: ConfigLoader[AccessKeyId] = (config: Config, path: String) => {
    AccessKeyId(string = config.getString(path))
  }
}
