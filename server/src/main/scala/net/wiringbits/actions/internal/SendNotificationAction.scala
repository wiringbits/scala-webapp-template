package net.wiringbits.actions.internal

import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.repositories.models.UserNotification
import net.wiringbits.repositories.{UserNotificationsRepository, UsersRepository}
import net.wiringbits.util.EmailMessage

import java.time.Clock
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

class SendNotificationAction @Inject() (
    emailApi: EmailApi,
    usersRepository: UsersRepository,
    userNotificationsRepository: UserNotificationsRepository
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(notification: UserNotification): Future[Unit] = {
    sendEmail(notification).onComplete {
      case Failure(ex) =>
        val minutesUntilExecute = getDelay(notification.errorCount).toMinutes
        val executeAt = clock.instant().plus(minutesUntilExecute, ChronoUnit.MINUTES)
        userNotificationsRepository.setStatusToFailed(notification.id, executeAt, ex.getMessage)
      case Success(_) => ()
    }
    Future.unit
  }

  private def sendEmail(notification: UserNotification): Future[Unit] = for {
    userMaybe <- usersRepository.find(notification.userId)
    user = userMaybe.getOrElse(throw new RuntimeException(s"User with id ${notification.userId} wasn't found"))
    emailMessage = EmailMessage(subject = notification.subject, body = notification.message)
    emailRequest = EmailRequest(user.email, emailMessage)
    _ <- emailApi.sendEmail(emailRequest)
    _ <- userNotificationsRepository.setStatusToSuccess(notification.id)
  } yield ()

  private def getDelay(
      retry: Int,
      factor: Int = 2,
      jitter: Int = Random.nextInt(100)
  ): FiniteDuration = {
    (Math
      .pow(
        factor.toDouble,
        retry.toDouble
      )
      .longValue + jitter).millis
  }
}
