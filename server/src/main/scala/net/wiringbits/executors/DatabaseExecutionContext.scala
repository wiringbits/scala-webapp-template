package net.wiringbits.executors

import org.apache.pekko.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

trait DatabaseExecutionContext extends ExecutionContext

object DatabaseExecutionContext {

  @Singleton
  class AkkaBased @Inject() (system: ActorSystem)
      extends CustomExecutionContext(system, "database.dispatcher")
      with DatabaseExecutionContext
}
