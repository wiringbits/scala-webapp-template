package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.repositories.models.{User, UserLog}
import net.wiringbits.typo_generated.public.user_logs.UserLogsRow
import net.wiringbits.typo_generated.public.users.UsersRow
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.{LoginUtils, RepositoryUtils}

import java.time.Instant
import java.util.UUID

class UserLogsRepositorySpec extends RepositorySpec with RepositoryUtils {
  "create" should {
    "work" in withRepositories() { implicit repositories =>
      val usersRow = createNonVerifyUser().futureValue

      val logsRequest = UserLogsRow(
        userLogId = UUID.randomUUID(),
        userId = usersRow.userId,
        message = "Test",
        createdAt = Instant.now
      )
      repositories.userLogs.create(logsRequest).futureValue
    }

    "fail if the user doesn't exists" in withRepositories() { implicit repositories =>
      val logsRequest = UserLogsRow(
        userLogId = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        message = "Test",
        createdAt = Instant.now
      )
      val ex = intercept[RuntimeException] {
        repositories.userLogs.create(logsRequest).futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "create(userId, message)" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      repositories.userLogs.create(request.userId, "test").futureValue
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
    "return every log" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val message = "test"
      for (_ <- 1 to 3) {
        repositories.userLogs.create(request.userId, message).futureValue
      }

      val response = repositories.userLogs.logs(request.userId).futureValue
      // Creating a user generates a user log. 3 + 1
      response.length must be(4)
    }

    "return no results" in withRepositories() { implicit repositories =>
      val response = repositories.userLogs.logs(UUID.randomUUID()).futureValue
      response.isEmpty must be(true)
    }
  }
}
