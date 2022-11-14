package net.wiringbits.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject
import net.wiringbits.actions.internal.{GetPendingNotificationsAction, SendNotificationAction}
import net.wiringbits.config.NotificationsConfig
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class NotificationsTask @Inject() (
    notificationsConfig: NotificationsConfig,
    getPendingNotifications: GetPendingNotificationsAction,
    sendNotificationAction: SendNotificationAction
)(implicit
    ec: ExecutionContext,
    actorSystem: ActorSystem
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting the notifications task")
  actorSystem.scheduler.scheduleOnce(
    notificationsConfig.interval
  ) {
    run()
  }

  def run(): Unit = {
    val result = for {
      stream <- getPendingNotifications()
      _ <- stream
        .throttle(100, 1.minute)
        .runWith(akka.stream.scaladsl.Sink.foreachAsync(1)(sendNotificationAction.apply))
    } yield ()

    result.onComplete {
      case Failure(ex) =>
        logger.error(
          s"Failed to process pending notifications, retrying after ${notificationsConfig.interval}: ${ex.getMessage}",
          ex
        )
        actorSystem.scheduler.scheduleOnce(notificationsConfig.interval) { run() }

      case Success(_) =>
        actorSystem.scheduler.scheduleOnce(notificationsConfig.interval) { run() }
    }
  }
}
