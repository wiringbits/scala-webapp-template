package net.wiringbits.modules

import com.google.inject.AbstractModule
import net.wiringbits.tasks.BackgroundJobsExecutorTask

class TasksModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[BackgroundJobsExecutorTask]).asEagerSingleton()
  }
}
