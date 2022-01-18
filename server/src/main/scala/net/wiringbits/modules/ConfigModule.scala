package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.{EmailConfig, WebAppConfig, JwtConfig, TokensConfig}
import play.api.Configuration

class ConfigModule extends AbstractModule {

  @Provides()
  def jwtConfig(global: Configuration): JwtConfig = {
    JwtConfig(global.get[Configuration]("jwt"))
  }

  @Provides()
  def emailConfig(global: Configuration): EmailConfig = {
    EmailConfig(global.get[Configuration]("email"))
  }

  @Provides()
  def tokensConfig(global: Configuration): TokensConfig = {
    TokensConfig(global.get[Configuration]("tokens"))
  }

  @Provides()
  def webAppConfig(global: Configuration): WebAppConfig = {
    WebAppConfig(global.get[Configuration]("webapp"))
  }
}
