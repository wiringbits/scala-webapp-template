package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.*
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
  def userTokensConfig(global: Configuration): UserTokensConfig = {
    val config = UserTokensConfig(global.get[Configuration]("userTokens"))
    logger.info(
      s"Loading userTokensConfig, emailVerificationExp = ${config.emailVerificationExp}, resetPasswordExp = ${config.resetPasswordExp}"
    )
    config
  }

  @Provides()
  def awsConfig(global: Configuration): AWSConfig = {
    val config = AWSConfig(global.get[Configuration]("aws"))
    logger.info(
      s"Loading AWSConfig, accessKeyId = ${config.accessKeyId}, secretAccessKey = ${config.secretAccessKey}, region = ${config.region}"
    )
    config
  }
}
