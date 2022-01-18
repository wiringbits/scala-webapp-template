package net.wiringbits.models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class JwtSecret(string: String) extends SecretValue(string)

object JwtSecret {

  implicit val configLoader: ConfigLoader[JwtSecret] = (config: Config, path: String) => {
    JwtSecret(string = config.getString(path))
  }
}
