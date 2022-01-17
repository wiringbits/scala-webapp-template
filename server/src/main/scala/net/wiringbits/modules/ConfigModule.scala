package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.{AwsConfig, FrontendConfig, JwtConfig, TokensConfig}
import play.api.Configuration

class ConfigModule extends AbstractModule {

  @Provides()
  def jwtConfig(global: Configuration): JwtConfig = {
    JwtConfig(global.get[Configuration]("jwt"))
  }

  @Provides()
  def awsConfig(global: Configuration): AwsConfig = {
    AwsConfig(global.get[Configuration]("aws"))
  }

  @Provides()
  def tokensConfig(global: Configuration): TokensConfig = {
    TokensConfig(global.get[Configuration]("tokens"))
  }

  @Provides()
  def frontendConfig(global: Configuration): FrontendConfig = {
    FrontendConfig(global.get[Configuration]("frontend"))
  }
}
