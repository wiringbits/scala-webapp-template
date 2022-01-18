package net.wiringbits.modules

import com.google.inject.AbstractModule

import java.time.Clock

class ClockModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Clock]).toInstance(Clock.systemUTC())
  }
}
