package net.wiringbits.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject
import net.wiringbits.actions.internal.{GetPendingNotificationsAction, SendNotificationAction}
import net.wiringbits.config.NotificationsConfig
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class NotificationsTask @Inject() (
    notificationsConfig: NotificationsConfig,
    getPendingNotifications: GetPendingNotificationsAction,
    sendNotificationAction: SendNotificationAction
)(implicit
    ec: ExecutionContext,
    actorSystem: ActorSystem
) {
  val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting the notifications task")
  actorSystem.scheduler.scheduleOnce(
    notificationsConfig.interval
  ) {
    run()
  }

  def run(): Unit = {
    getPendingNotifications()
      .onComplete {
        case Failure(exception) => logger.error("Failed to get notifications", exception)
        case Success(notifications) =>
          val message = s"There's ${notifications.size} pending notifications"
          if (notifications.isEmpty) logger.trace(message)
          else logger.info(message)
          notifications.foreach { notification =>
            sendNotificationAction(notification).onComplete {
              case Failure(ex) =>
                logger.info(s"There was an error trying to send notification with id = ${notification.id}", ex)
              case Success(_) => ()
            }
          }
      }

    actorSystem.scheduler.scheduleOnce(notificationsConfig.interval) { run() }
    ()
  }
}
