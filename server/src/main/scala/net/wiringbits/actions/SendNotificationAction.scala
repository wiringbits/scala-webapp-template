package net.wiringbits.actions

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.NotificationsConfig
import net.wiringbits.repositories.models.UserNotification
import net.wiringbits.repositories.{UserNotificationsRepository, UsersRepository}
import net.wiringbits.util.EmailMessage

import java.time.Clock
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SendNotificationAction @Inject() (
    emailApi: EmailApi,
    usersRepository: UsersRepository,
    userNotificationsRepository: UserNotificationsRepository,
    notificationsConfig: NotificationsConfig
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(notification: UserNotification): Future[Unit] = {
    try {
      sendEmail(notification)
    } catch {
      case ex: Exception =>
        val executeAt = clock.instant().plus(notificationsConfig.delayIfFailure.toHours, ChronoUnit.HOURS)
        userNotificationsRepository.setStatusToFailed(notification.id, executeAt, ex.getMessage)
    }
  }

  private def sendEmail(notification: UserNotification): Future[Unit] = for {
    userMaybe <- usersRepository.find(notification.userId)
    user = userMaybe.getOrElse(throw new RuntimeException(s"User with id ${notification.userId} wasn't found"))
    emailMessage = EmailMessage(subject = notification.subject, body = notification.message)
    emailRequest = EmailRequest(user.email, emailMessage)
    _ = emailApi.sendEmail(emailRequest)
    _ <- userNotificationsRepository.setStatusToSuccess(notification.id)
  } yield ()
}
