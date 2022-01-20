package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.{AWSRegionConfig, EmailConfig, JwtConfig, ReCaptchaConfig, WebAppConfig}
import org.slf4j.LoggerFactory
import play.api.Configuration

class ConfigModule extends AbstractModule {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @Provides()
  def jwtConfig(global: Configuration): JwtConfig = {
    val config = JwtConfig(global.get[Configuration]("jwt"))
    logger.info(s"Loading jwtConfig, secret = ${config.secret}")
    config
  }

  @Provides()
  def recaptchaConfig(global: Configuration): ReCaptchaConfig = {
    val config = ReCaptchaConfig(global.get[Configuration]("recaptcha"))
    logger.info(s"Loading reCaptchaConfig, secret = ${config.secret}")
    config
  }

  @Provides()
  def emailConfig(global: Configuration): EmailConfig = {
    val config = EmailConfig(global.get[Configuration]("email"))
    logger.info(s"Loading emailConfig, senderAddress = ${config.senderAddress}")
    config
  }

  @Provides()
  def webAppConfig(global: Configuration): WebAppConfig = {
    val config = WebAppConfig(global.get[Configuration]("webapp"))
    logger.info(s"Loading webAppConfig, host = ${config.host}")
    config
  }

  @Provides()
  def awsRegionConfig(global: Configuration): AWSRegionConfig = {
    val config = AWSRegionConfig(global.get[Configuration]("aws"))
    logger.info(s"Loading AWSRegionConfig, region = ${config.region}")
    config
  }
}
