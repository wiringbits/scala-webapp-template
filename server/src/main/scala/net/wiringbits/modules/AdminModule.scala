package net.wiringbits.modules

import net.wiringbits.config.AdminConfig
import play.api.inject
import play.api.inject.SimpleModule

class AdminModule extends SimpleModule(inject.bind[AdminConfig].toSelf.eagerly())
