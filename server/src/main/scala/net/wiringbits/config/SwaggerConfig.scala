package net.wiringbits.config

import play.api.Configuration

case class SwaggerConfig(basePath: String, info: SwaggerConfig.Info) {
  override def toString: String = s"SwaggerConfig($basePath, $info)"
}

object SwaggerConfig {
  case class Info(version: String, contact: String, title: String, description: String) {
    override def toString: String = s"Info($version, $contact, $title, $description)"
  }

  def apply(config: Configuration): SwaggerConfig = {
    val apiConfig = config.get[Configuration]("api")
    val apiInfoConfig = apiConfig.get[Configuration]("info")

    val basePath = apiConfig.get[String]("basePath")
    val version = apiInfoConfig.get[String]("version")
    val contact = apiInfoConfig.get[String]("contact")
    val title = apiInfoConfig.get[String]("title")
    val description = apiInfoConfig.get[String]("description")
    SwaggerConfig(basePath, Info(version, contact, title, description))
  }
}
