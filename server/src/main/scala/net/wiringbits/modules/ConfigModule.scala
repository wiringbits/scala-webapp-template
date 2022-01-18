package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.{JwtConfig, ReCaptchaConfig}
import play.api.Configuration

class ConfigModule extends AbstractModule {

  @Provides()
  def jwtConfig(global: Configuration): JwtConfig = {
    JwtConfig(global.get[Configuration]("jwt"))
  }

  @Provides()
  def recaptchaConfig(global: Configuration): ReCaptchaConfig = {
    ReCaptchaConfig(global.get[Configuration]("recaptcha"))
  }
}
