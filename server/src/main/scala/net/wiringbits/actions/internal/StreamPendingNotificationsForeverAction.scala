package net.wiringbits.actions.internal

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import net.wiringbits.repositories.UserNotificationsRepository
import net.wiringbits.repositories.models.UserNotification
import org.slf4j.LoggerFactory

import javax.inject.Inject
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

class StreamPendingNotificationsForeverAction @Inject() (userNotificationsRepository: UserNotificationsRepository)(
    implicit
    ec: ExecutionContext,
    system: ActorSystem
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def apply(reconnectionDelay: FiniteDuration = 10.seconds): Source[UserNotification, akka.NotUsed] = {
    // Let's use unfoldAsync to continuously fetch items from database
    // First execution doesn't involve a delay
    Source
      .unfoldAsync[Boolean, Source[UserNotification, Future[Int]]](false) { delay =>
        logger.trace(s"Looking for pending notifications")
        akka.pattern
          .after(if (delay) reconnectionDelay else 0.seconds) {
            userNotificationsRepository.streamPendingNotifications
          }
          .map(source => Some(true -> source))
      }
      .flatMapConcat(identity)
  }
}
