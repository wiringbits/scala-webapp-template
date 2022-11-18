package net.wiringbits.actions.internal

import net.wiringbits.repositories.UserNotificationsRepository
import net.wiringbits.repositories.models.UserNotification

import javax.inject.Inject
import scala.concurrent.Future

class StreamPendingNotificationsAction @Inject() (userNotificationsRepository: UserNotificationsRepository) {
  def apply(): Future[akka.stream.scaladsl.Source[UserNotification, Future[Int]]] = {
    userNotificationsRepository.streamPendingNotifications
  }
}
