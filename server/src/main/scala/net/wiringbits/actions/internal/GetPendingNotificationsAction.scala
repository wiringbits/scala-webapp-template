package net.wiringbits.actions.internal

import net.wiringbits.repositories.UserNotificationsRepository
import net.wiringbits.repositories.models.UserNotification

import javax.inject.Inject
import scala.concurrent.Future

class GetPendingNotificationsAction @Inject() (userNotificationsRepository: UserNotificationsRepository) {
  def apply(): Future[List[UserNotification]] = {
    userNotificationsRepository.getPendingNotifications
  }
}
