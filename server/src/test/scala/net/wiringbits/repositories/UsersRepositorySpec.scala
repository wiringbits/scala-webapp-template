package net.wiringbits.repositories

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.repositories.models.User
import net.wiringbits.util.EmailMessage
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.must.Matchers._

import java.util.UUID

class UsersRepositorySpec extends RepositorySpec with BeforeAndAfterAll {

  // required to test the streaming operations
  private implicit lazy val system: ActorSystem = ActorSystem("UserNotificationsRepositorySpec")

  override def afterAll(): Unit = {
    system.terminate().futureValue
    super.afterAll()
  }

  "create" should {
    "work" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue
    }

    "create a token for verifying the email" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val response = repositories.userTokens.find(request.id).futureValue
      response.nonEmpty must be(true)
    }

    "fail when the id already exists" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )

      repositories.users.create(request).futureValue
      val ex = intercept[RuntimeException] {
        repositories.users.create(request.copy(email = Email.trusted("email2@wiringbits.net"))).futureValue
      }
      // TODO: This should be a better message
      ex.getCause.getMessage must startWith(
        """ERROR: duplicate key value violates unique constraint "users_user_id_pk""""
      )
    }

    "fail when the email already exists" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )

      repositories.users.create(request).futureValue
      val ex = intercept[RuntimeException] {
        repositories.users.create(request.copy(id = UUID.randomUUID())).futureValue
      }
      // TODO: This should be a better message
      ex.getCause.getMessage must startWith(
        """ERROR: duplicate key value violates unique constraint "users_email_unique""""
      )
    }
  }

  "all" should {
    "return the existing users" in withRepositories() { repositories =>
      for (i <- 1 to 3) {
        val request = User.CreateUser(
          id = UUID.randomUUID(),
          email = Email.trusted(s"test$i@wiringbits.net"),
          name = Name.trusted("Sample"),
          hashedPassword = "password",
          verifyEmailToken = "token"
        )
        repositories.users.create(request).futureValue
      }

      val response = repositories.users.all().futureValue
      response.length must be(3)
    }

    "return no users" in withRepositories() { repositories =>
      val response = repositories.users.all().futureValue
      response.isEmpty must be(true)
    }
  }

  "find(email)" should {
    "return a user when the email exists" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val response = repositories.users.find(request.email).futureValue
      response.value.email must be(request.email)
      response.value.id must be(request.id)
      response.value.hashedPassword must be(request.hashedPassword)
    }

    "return a user when the email exists (case insensitive match)" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val email = Email.trusted(request.email.string.toUpperCase)
      val response = repositories.users.find(email).futureValue
      response.isDefined must be(true)
    }

    "return no result when the email doesn't exists" in withRepositories() { repositories =>
      val email = Email.trusted("hello@wiringbits.net")
      val response = repositories.users.find(email).futureValue
      response.isEmpty must be(true)
    }
  }

  "find(id)" should {
    "return a user when the id exists" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.email must be(request.email)
      response.value.id must be(request.id)
      response.value.hashedPassword must be(request.hashedPassword)
    }

    "return no result when the id doesn't exists" in withRepositories() { repositories =>
      val id = UUID.randomUUID()
      val response = repositories.users.find(id).futureValue
      response.isEmpty must be(true)
    }
  }

  "update" should {
    "update an existing user" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val newName = Name.trusted("Test")
      repositories.users.update(request.id, newName).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.name must be(newName)
      response.value.email must be(request.email)
    }

    "fail when the user doesn't exist" in withRepositories() { repositories =>
      val id = UUID.randomUUID()
      val newName = Name.trusted("Test")
      val ex = intercept[RuntimeException] {
        repositories.users.update(id, newName).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "updatePassword" should {
    "update the password for an existing user" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val newPassword = "test"
      repositories.users.updatePassword(request.id, newPassword, EmailMessage.updatePassword(request.name)).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.hashedPassword must be(newPassword)
    }

    "produce a notification for the user" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val newPassword = "test"
      repositories.users.updatePassword(request.id, newPassword, EmailMessage.updatePassword(request.name)).futureValue

      val response = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(1)
    }

    "fail when the user doesn't exist" in withRepositories() { repositories =>
      val name = Name.trusted("test")
      val ex = intercept[RuntimeException] {
        repositories.users.updatePassword(UUID.randomUUID(), "test", EmailMessage.updatePassword(name)).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "verify" should {
    "verify a user given a token" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue
      repositories.users.verify(request.id, UUID.randomUUID(), EmailMessage.confirm(request.name)).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.verifiedOn.isDefined must be(true)
    }

    "produce a notification for the user" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue
      repositories.users.verify(request.id, UUID.randomUUID(), EmailMessage.confirm(request.name)).futureValue

      val response = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(1)
    }

    "fail when the user doesn't exist" in withRepositories() { repositories =>
      val name = Name.trusted("test")
      val ex = intercept[RuntimeException] {
        repositories.users.verify(UUID.randomUUID(), UUID.randomUUID(), EmailMessage.confirm(name)).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "resetPassword" should {
    "update the password" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val newPassword = "test"
      repositories.users.resetPassword(request.id, newPassword, EmailMessage.resetPassword(request.name)).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.hashedPassword must be(newPassword)
    }

    "produce a notification for the user" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val newPassword = "test"
      repositories.users.resetPassword(request.id, newPassword, EmailMessage.resetPassword(request.name)).futureValue

      val response = repositories.userNotifications.streamPendingNotifications.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(1)
    }

    "fail when the user doesn't exist" in withRepositories() { repositories =>
      val name = Name.trusted("test")
      val ex = intercept[RuntimeException] {
        repositories.users.resetPassword(UUID.randomUUID(), "test", EmailMessage.resetPassword(name)).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }
}
