package net.wiringbits.modules

import com.google.inject.AbstractModule
import net.wiringbits.tasks.{ExpiredTokensTask, NotificationsTask}

class TasksModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[NotificationsTask]).asEagerSingleton()
    bind(classOf[ExpiredTokensTask]).asEagerSingleton()
  }
}
