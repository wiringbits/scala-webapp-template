package net.wiringbits.modules

import net.wiringbits.config.AdminConfig
import play.api.inject.{SimpleModule, bind}

class DataExplorerModule extends SimpleModule(bind[AdminConfig].toSelf.eagerly())
