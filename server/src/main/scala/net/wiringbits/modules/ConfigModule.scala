package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.{JwtConfig, ReCaptchaConfig}
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
}
