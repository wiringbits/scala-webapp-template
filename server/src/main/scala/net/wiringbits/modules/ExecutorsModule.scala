package net.wiringbits.modules

import com.google.inject.AbstractModule
import net.wiringbits.executors.DatabaseExecutionContext

class ExecutorsModule extends AbstractModule {

  override def configure(): Unit = {
    val _ = bind(classOf[DatabaseExecutionContext]).to(classOf[DatabaseExecutionContext.AkkaBased]).asEagerSingleton()
  }
}
