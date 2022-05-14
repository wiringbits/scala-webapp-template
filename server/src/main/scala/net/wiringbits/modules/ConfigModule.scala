package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config._
import org.slf4j.LoggerFactory
import play.api.Configuration

import javax.inject.Singleton

class ConfigModule extends AbstractModule {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @Provides
  @Singleton
  def recaptchaConfig(global: Configuration): ReCaptchaConfig = {
    val config = ReCaptchaConfig(global.get[Configuration]("recaptcha"))
    logger.info(s"Config loaded: $config")
    config
  }

  @Provides
  @Singleton
  def emailConfig(global: Configuration): EmailConfig = {
    val config = EmailConfig(global.get[Configuration]("email"))
    logger.info(s"Config loaded: $config")
    config
  }

  @Provides
  @Singleton
  def webAppConfig(global: Configuration): WebAppConfig = {
    val config = WebAppConfig(global.get[Configuration]("webapp"))
    logger.info(s"Config loaded: $config")
    config
  }

  @Provides
  @Singleton
  def userTokensConfig(global: Configuration): UserTokensConfig = {
    val config = UserTokensConfig(global.get[Configuration]("userTokens"))
    logger.info(s"Config loaded: $config")
    config
  }

  @Provides
  @Singleton
  def awsConfig(global: Configuration): AWSConfig = {
    val config = AWSConfig(global.get[Configuration]("aws"))
    logger.info(s"Config loaded: $config")
    config
  }

  @Provides
  @Singleton
  def notificationsConfig(global: Configuration): NotificationsConfig = {
    val config = NotificationsConfig(global.get[Configuration]("notificationsTask"))
    logger.info(s"Config loaded: $config")
    config
  }
}
