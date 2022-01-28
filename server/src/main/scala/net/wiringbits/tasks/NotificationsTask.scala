package net.wiringbits.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject
import net.wiringbits.actions.{GetPendingNotificationsAction, SendNotificationAction}
import net.wiringbits.config.NotificationsConfig
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

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
  actorSystem.scheduler.scheduleAtFixedRate(
    notificationsConfig.delayedInit,
    notificationsConfig.interval
  ) { () =>
    run()
  }

  def run(): Unit = {
    getPendingNotifications.apply.foreach { notifications =>
      logger.info(s"There's ${notifications.size} pending notifications")
      notifications.foreach(notification => sendNotificationAction(notification))
    }
  }
}
