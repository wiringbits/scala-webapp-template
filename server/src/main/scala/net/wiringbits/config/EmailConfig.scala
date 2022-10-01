package net.wiringbits.config

import play.api.Configuration

case class EmailConfig(senderAddress: String, provider: String) {
  override def toString: String = {
    s"EmailConfig(senderAddress = $senderAddress, provider = $provider)"
  }
}

object EmailConfig {
  def apply(config: Configuration): EmailConfig = {
    val senderAddress = config.get[String]("senderAddress")
    val provider = config.get[String]("provider")
    new EmailConfig(senderAddress = senderAddress, provider = provider)
  }
}
