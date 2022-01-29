package net.wiringbits.actions.internal

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.repositories.models.UserNotification
import net.wiringbits.repositories.{UserNotificationsRepository, UsersRepository}
import net.wiringbits.util.{DelayGenerator, EmailMessage}
import org.slf4j.LoggerFactory

import java.time.Clock
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class SendNotificationAction @Inject() (
    emailApi: EmailApi,
    usersRepository: UsersRepository,
    userNotificationsRepository: UserNotificationsRepository
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def apply(notification: UserNotification): Future[Unit] = {
    sendEmail(notification).recoverWith { case NonFatal(ex) =>
      val minutesUntilExecute = DelayGenerator.createDelay(notification.errorCount)
      val executeAt = clock.instant().plus(minutesUntilExecute, ChronoUnit.MINUTES)
      logger.warn(s"Notification with id ${notification.id} failed", ex)
      userNotificationsRepository.setStatusToFailed(notification.id, executeAt, ex.getMessage)
    }
  }

  private def sendEmail(notification: UserNotification): Future[Unit] = for {
    userMaybe <- usersRepository.find(notification.userId)
    user = userMaybe.getOrElse(throw new RuntimeException(s"User with id ${notification.userId} wasn't found"))
    emailMessage = EmailMessage(subject = notification.subject, body = notification.message)
    emailRequest = EmailRequest(user.email, emailMessage)
    _ <- emailApi.sendEmail(emailRequest)
    _ <- userNotificationsRepository.setStatusToSuccess(notification.id)
  } yield ()
}
