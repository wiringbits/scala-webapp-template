package net.wiringbits.repositories

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import net.wiringbits.common.models.id.{UserId, UserTokenId}
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.util.EmailMessage
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.RepositoryUtils

import java.util.UUID

class UsersRepositorySpec extends RepositorySpec with BeforeAndAfterAll with RepositoryUtils {

  // required to test the streaming operations
  private implicit lazy val system: ActorSystem = ActorSystem("UserRepositorySpec")

  override def afterAll(): Unit = {
    system.terminate().futureValue
    super.afterAll()
  }

  "create" should {
    "work" in withRepositories() { implicit repositories =>
      createNonVerifyUser().futureValue
    }

    "create a token for verifying the email" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val response = repositories.userTokens.find(request.userId).futureValue
      response.nonEmpty must be(true)
    }

    "fail when the id already exists" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue
      val ex = intercept[RuntimeException] {
        createNonVerifyUser(
          userIdMaybe = Some(request.userId),
          emailMaybe = Some(Email.trusted("email2@wiringbits.net"))
        ).futureValue
      }
      // TODO: This should be a better message
      ex.getCause.getMessage must startWith(
        """ERROR: duplicate key value violates unique constraint "users_user_id_pk""""
      )
    }

    "fail when the email already exists" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val ex = intercept[RuntimeException] {
        createNonVerifyUser(emailMaybe = Some(request.email)).futureValue
      }
      // TODO: This should be a better message
      ex.getCause.getMessage must startWith(
        """ERROR: duplicate key value violates unique constraint "users_email_unique""""
      )
    }
  }

  "all" should {
    "return the existing users" in withRepositories() { implicit repositories =>
      for (i <- 1 to 3) {
        createNonVerifyUser(emailMaybe = Some(Email.trusted(s"test$i@wiringbits.net"))).futureValue
      }

      val response = repositories.users.all().futureValue
      response.length must be(3)
    }

    "return no users" in withRepositories() { implicit repositories =>
      val response = repositories.users.all().futureValue
      response.isEmpty must be(true)
    }
  }

  "find(email)" should {
    "return a user when the email exists" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val response = repositories.users.find(request.email).futureValue.value
      response.email must be(request.email)
      response.userId must be(request.userId)
      response.password must be(request.password)
    }

    "return a user when the email exists (case insensitive match)" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val email = Email.trusted(request.email.string.toUpperCase)
      val response = repositories.users.find(email).futureValue
      response.isDefined must be(true)
    }

    "return no result when the email doesn't exists" in withRepositories() { implicit repositories =>
      val email = Email.trusted("hello@wiringbits.net")
      val response = repositories.users.find(email).futureValue
      response.isEmpty must be(true)
    }
  }

  "find(id)" should {
    "return a user when the id exists" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val response = repositories.users.find(request.userId).futureValue.value
      response.email must be(request.email)
      response.userId must be(request.userId)
      response.password must be(request.password)
    }

    "return no result when the id doesn't exists" in withRepositories() { implicit repositories =>
      val response = repositories.users.find(UserId.randomUUID).futureValue
      response.isEmpty must be(true)
    }
  }

  "update" should {
    "update an existing user" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val newName = Name.trusted("Test")
      repositories.users.update(request.userId, newName).futureValue

      val response = repositories.users.find(request.userId).futureValue.value
      response.name must be(newName)
      response.email must be(request.email)
    }

    "fail when the user doesn't exist" in withRepositories() { implicit repositories =>
      val newName = Name.trusted("Test")
      val ex = intercept[RuntimeException] {
        repositories.users.update(UserId.randomUUID, newName).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "updatePassword" should {
    "update the password for an existing user" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val newPassword = "test"
      repositories.users
        .updatePassword(request.userId, newPassword, EmailMessage.updatePassword(request.name))
        .futureValue

      val response = repositories.users.find(request.userId).futureValue
      response.value.password must be(newPassword)
    }

    "produce a notification for the user" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val newPassword = "test"
      repositories.users
        .updatePassword(request.userId, newPassword, EmailMessage.updatePassword(request.name))
        .futureValue

      val response = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(1)
    }

    "fail when the user doesn't exist" in withRepositories() { implicit repositories =>
      val name = Name.trusted("test")
      val ex = intercept[RuntimeException] {
        repositories.users
          .updatePassword(UserId.randomUUID, "test", EmailMessage.updatePassword(name))
          .futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "verify" should {
    "verify a user given a token" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue
      repositories.users
        .verify(request.userId, UserTokenId.randomUUID, EmailMessage.confirm(request.name))
        .futureValue

      val response = repositories.users.find(request.userId).futureValue
      response.value.verifiedOn.isDefined must be(true)
    }

    "produce a notification for the user" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue
      repositories.users
        .verify(request.userId, UserTokenId.randomUUID, EmailMessage.confirm(request.name))
        .futureValue

      val response = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(1)
    }

    "fail when the user doesn't exist" in withRepositories() { repositories =>
      val name = Name.trusted("test")
      val ex = intercept[RuntimeException] {
        repositories.users
          .verify(UserId.randomUUID, UserTokenId.randomUUID, EmailMessage.confirm(name))
          .futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "resetPassword" should {
    "update the password" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val newPassword = "test"
      repositories.users
        .resetPassword(request.userId, newPassword, EmailMessage.resetPassword(request.name))
        .futureValue

      val response = repositories.users.find(request.userId).futureValue
      response.value.password must be(newPassword)
    }

    "produce a notification for the user" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val newPassword = "test"
      repositories.users
        .resetPassword(request.userId, newPassword, EmailMessage.resetPassword(request.name))
        .futureValue

      val response = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(1)
    }

    "fail when the user doesn't exist" in withRepositories() { repositories =>
      val name = Name.trusted("test")
      val ex = intercept[RuntimeException] {
        repositories.users
          .resetPassword(UserId.randomUUID, "test", EmailMessage.resetPassword(name))
          .futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }
}
