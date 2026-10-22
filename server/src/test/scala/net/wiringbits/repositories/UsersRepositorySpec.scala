package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.{RepositoryComponents, RepositorySpec}
import net.wiringbits.models.jobs.BackgroundJobType
import net.wiringbits.repositories.daos.backgroundJobParser
import net.wiringbits.repositories.models.{BackgroundJobData, User}
import net.wiringbits.util.EmailMessage
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.RepositoryUtils

import java.util.UUID

class UsersRepositorySpec extends RepositorySpec with RepositoryUtils {

  // No repository API reads background jobs back, so inspect the stored rows for the given recipient directly.
  private def sendEmailJobsOf(email: Email)(using repositories: RepositoryComponents): List[BackgroundJobData] =
    repositories.database.withConnection { implicit conn =>
      anorm
        .SQL("SELECT * FROM background_jobs WHERE payload->>'email' = {email}")
        .on("email" -> email.string)
        .as(backgroundJobParser.*)
    }

  "create" should {
    "work" in withRepositories() { implicit repositories =>
      createUser().futureValue
    }

    "create a token for verifying the email" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val response = repositories.userTokens.find(request.id).futureValue
      response.nonEmpty must be(true)
    }

    "fail when the id already exists" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue
      val ex = intercept[RuntimeException] {
        repositories.users.create(request.copy(email = Email.trusted("email2@wiringbits.net"))).futureValue
      }
      // TODO: This should be a better message
      ex.getCause.getMessage must startWith(
        """ERROR: duplicate key value violates unique constraint "users_user_id_pk""""
      )
    }

    "fail when the email already exists" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue
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
    // users.all() returns every user in the database, and the suite shares one Postgres container, so this checks
    // that the users created here are present rather than asserting an absolute count.
    "return the existing users" in withRepositories() { implicit repositories =>
      val expected = List.fill(3)(createUser().futureValue)

      val response = repositories.users.all().futureValue
      response.map(u => (u.id, u.email)) must contain allElementsOf expected.map(u => (u.id, u.email))
    }
  }

  "find(email)" should {
    "return a user when the email exists" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val response = repositories.users.find(request.email).futureValue
      response.value.email must be(request.email)
      response.value.id must be(request.id)
      response.value.hashedPassword must be(request.hashedPassword)
    }

    "return a user when the email exists (case insensitive match)" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

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
    "return a user when the id exists" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

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
    "update an existing user" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

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
    "update the password for an existing user" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val newPassword = "test"
      repositories.users.updatePassword(request.id, newPassword, EmailMessage.updatePassword(request.name)).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.hashedPassword must be(newPassword)
    }

    "produce a notification for the user" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val newPassword = "test"
      repositories.users.updatePassword(request.id, newPassword, EmailMessage.updatePassword(request.name)).futureValue

      sendEmailJobsOf(request.email).map(_.`type`) must be(List(BackgroundJobType.SendEmail))
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
    "verify a user given a token" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue
      repositories.users.verify(request.id, UUID.randomUUID(), EmailMessage.confirm(request.name)).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.verifiedOn.isDefined must be(true)
    }

    "produce a notification for the user" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue
      repositories.users.verify(request.id, UUID.randomUUID(), EmailMessage.confirm(request.name)).futureValue

      sendEmailJobsOf(request.email).map(_.`type`) must be(List(BackgroundJobType.SendEmail))
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
    "update the password" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val newPassword = "test"
      repositories.users.resetPassword(request.id, newPassword, EmailMessage.resetPassword(request.name)).futureValue

      val response = repositories.users.find(request.id).futureValue
      response.value.hashedPassword must be(newPassword)
    }

    "produce a notification for the user" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val newPassword = "test"
      repositories.users.resetPassword(request.id, newPassword, EmailMessage.resetPassword(request.name)).futureValue

      sendEmailJobsOf(request.email).map(_.`type`) must be(List(BackgroundJobType.SendEmail))
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
