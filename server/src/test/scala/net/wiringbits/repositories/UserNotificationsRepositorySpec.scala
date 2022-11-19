package net.wiringbits.repositories

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.repositories.daos.UserNotificationsDAO
import net.wiringbits.repositories.models.{NotificationStatus, NotificationType, User, UserNotification}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.must.Matchers._

import java.time.Instant
import java.util.UUID

class UserNotificationsRepositorySpec extends RepositorySpec with BeforeAndAfterAll {

  // required to test the streaming operations
  private implicit lazy val system: ActorSystem = ActorSystem("UserNotificationsRepositorySpec")

  override def afterAll(): Unit = {
    system.terminate().futureValue
    super.afterAll()
  }

  "streamPendingNotifications" should {

    "return pending notifications" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val notificationRequest = UserNotification.Create(
        id = UUID.randomUUID(),
        userId = request.id,
        notificationType = NotificationType.PasswordReset,
        subject = "",
        message = "",
        status = NotificationStatus.Pending,
        executeAt = Instant.now(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
      )

      repositories.database.withConnection { implicit conn =>
        UserNotificationsDAO.create(notificationRequest)
      }

      val result = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      result.size must be(1)
      val item = result.headOption.value
      item.status must be(notificationRequest.status)
      item.notificationType must be(notificationRequest.notificationType)
      item.message must be(notificationRequest.message)
      item.subject must be(notificationRequest.subject)
    }

    "only return pending notifications" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val notificationRequest = UserNotification.Create(
        id = UUID.randomUUID(),
        userId = request.id,
        notificationType = NotificationType.PasswordUpdated,
        subject = "",
        message = "",
        status = NotificationStatus.Pending,
        executeAt = Instant.now(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
      )

      val limit = 6
      for (i <- 1 to limit) {
        repositories.database.withConnection { implicit conn =>
          UserNotificationsDAO.create(
            notificationRequest.copy(
              id = UUID.randomUUID(),
              status = if ((i % 2) == 0) NotificationStatus.Success else NotificationStatus.Pending
            )
          )
        }
      }
      val response = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(limit / 2)
      response.foreach { x =>
        x.status must be(NotificationStatus.Pending)
        x.notificationType must be(notificationRequest.notificationType)
        x.message must be(notificationRequest.message)
        x.subject must be(notificationRequest.subject)
      }
    }

    "return no results" in withRepositories() { repositories =>
      val response = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.isEmpty must be(true)
    }
  }

  "setStatusToFailed" should {
    "work" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val notificationRequest = UserNotification.Create(
        id = UUID.randomUUID(),
        userId = request.id,
        notificationType = NotificationType.PasswordReset,
        subject = "",
        message = "",
        status = NotificationStatus.Pending,
        executeAt = Instant.now(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
      )

      repositories.database.withConnection { implicit conn =>
        UserNotificationsDAO.create(notificationRequest)
      }
      val failReason = "test"
      repositories.userNotifications
        .setStatusToFailed(notificationRequest.id, executeAt = Instant.now(), failReason = failReason)
        .futureValue
      val result = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue

      result.size must be(1)
      val item = result.headOption.value
      item.id must be(notificationRequest.id)
      item.status must be(NotificationStatus.Failed)
      item.statusDetails must be(Some(failReason))
    }

    "fail if the notification doesn't exists" in withRepositories() { repositories =>
      repositories.userNotifications
        .setStatusToFailed(UUID.randomUUID(), executeAt = Instant.now(), failReason = "test")
        .futureValue
    }
  }

  "setStatusToSuccess" should {
    "work" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val notificationRequest = UserNotification.Create(
        id = UUID.randomUUID(),
        userId = request.id,
        notificationType = NotificationType.PasswordUpdated,
        subject = "",
        message = "",
        status = NotificationStatus.Pending,
        executeAt = Instant.now(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
      )

      repositories.database.withConnection { implicit conn =>
        UserNotificationsDAO.create(notificationRequest)
      }
      repositories.userNotifications.setStatusToSuccess(notificationRequest.id).futureValue

      val result = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      result.isEmpty must be(true)
    }

    "fail if the notification doesn't exists" in withRepositories() { repositories =>
      repositories.userNotifications.setStatusToSuccess(UUID.randomUUID()).futureValue
    }
  }
}
