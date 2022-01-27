package net.wiringbits.config

import play.api.Configuration

case class EmailConfig(senderAddress: String) {
  override def toString: String = {
    s"EmailConfig(senderAddress = $senderAddress)"
  }
}

object EmailConfig {
  def apply(config: Configuration): EmailConfig = {
    val senderAddress = config.get[String]("senderAddress")
    EmailConfig(senderAddress)
  }
}
