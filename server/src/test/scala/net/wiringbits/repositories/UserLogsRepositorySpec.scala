package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.repositories.models.{User, UserLog}
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.must.Matchers._

import java.util.UUID

class UserLogsRepositorySpec extends RepositorySpec {
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

      val logsRequest = UserLog.CreateUserLog(userLogId = UUID.randomUUID(), userId = request.id, message = "test")
      repositories.userLogs.create(logsRequest).futureValue
    }

    "fail if the user doesn't exists" in withRepositories() { repositories =>
      val logsRequest =
        UserLog.CreateUserLog(userLogId = UUID.randomUUID(), userId = UUID.randomUUID(), message = "test")
      val ex = intercept[RuntimeException] {
        repositories.userLogs.create(logsRequest).futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "create(userId, message)" should {
    "work" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      repositories.userLogs.create(request.id, "test").futureValue
    }

    "fail if the user doesn't exists" in withRepositories() { repositories =>
      val ex = intercept[RuntimeException] {
        repositories.userLogs.create(UUID.randomUUID(), "test").futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "logs" should {
    "return every log" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val message = "test"
      for (_ <- 1 to 3) {
        repositories.userLogs.create(request.id, message).futureValue
      }

      val response = repositories.userLogs.logs(request.id).futureValue
      // Creating a user generates a user log. 3 + 1
      response.length must be(4)
    }

    "return no results" in withRepositories() { repositories =>
      val response = repositories.userLogs.logs(UUID.randomUUID()).futureValue
      response.isEmpty must be(true)
    }
  }
}
