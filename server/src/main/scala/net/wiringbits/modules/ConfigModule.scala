package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.config.JwtConfig
import play.api.Configuration

class ConfigModule extends AbstractModule {

  @Provides()
  def jwtConfig(global: Configuration): JwtConfig = {
    JwtConfig(global.get[Configuration]("jwt"))
  }
}
