package net.wiringbits.tasks

import akka.actor.ActorSystem
import net.wiringbits.actions.internal.{DeleteExpiredTokenAction, GetExpiredTokensAction}
import net.wiringbits.config.ExpiredTokensConfig
import org.slf4j.LoggerFactory

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ExpiredTokensTask @Inject() (
    expiredTokensConfig: ExpiredTokensConfig,
    getExpiredTokens: GetExpiredTokensAction,
    deleteExpiredTokenAction: DeleteExpiredTokenAction
)(implicit
    ec: ExecutionContext,
    actorSystem: ActorSystem
) {
  val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting the expired tokens task")
  actorSystem.scheduler.scheduleOnce(
    expiredTokensConfig.interval
  ) {
    run()
  }

  def run(): Unit = {
    getExpiredTokens()
      .onComplete {
        case Failure(exception) => logger.error("Failed to get expired tokens", exception)
        case Success(expiredTokens) =>
          val message = s"There's ${expiredTokens.size} expired tokens"
          if (expiredTokens.isEmpty) logger.trace(message)
          else logger.info(message)
          expiredTokens.foreach { expiredToken =>
            deleteExpiredTokenAction(expiredToken).onComplete {
              case Failure(ex) =>
                logger.info(s"There was an error trying to send notification with id = ${expiredToken.id}", ex)
              case Success(_) => ()
            }
          }
      }

    actorSystem.scheduler.scheduleOnce(expiredTokensConfig.interval) { run() }
    ()
  }
}
