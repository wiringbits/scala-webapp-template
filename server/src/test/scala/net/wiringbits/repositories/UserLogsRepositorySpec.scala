package net.wiringbits.repositories

import net.wiringbits.core.RepositorySpec
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.RepositoryUtils

import java.util.UUID

class UserLogsRepositorySpec extends RepositorySpec with RepositoryUtils {
  "create" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      createUserLog(request.id).futureValue
    }

    "fail if the user doesn't exists" in withRepositories() { implicit repositories =>
      val ex = intercept[RuntimeException] {
        createUserLog(UUID.randomUUID()).futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "create(userId, message)" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      createUserLog(request.id, "test").futureValue
    }

    "fail if the user doesn't exists" in withRepositories() { implicit repositories =>
      val ex = intercept[RuntimeException] {
        createUserLog(UUID.randomUUID(), "test").futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_logs" violates foreign key constraint "user_logs_users_fk""""
      )
    }
  }

  "logs" should {
    "return every log" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val message = "test"
      val expected = 3
      (1 to expected).foreach { _ =>
        createUserLog(request.id, message).futureValue
      }

      val response = repositories.userLogs.logs(request.id).futureValue
      // Creating a user generates a user log. 3 + 1
      response.length must be(expected + 1)
    }

    "return no results" in withRepositories() { repositories =>
      val response = repositories.userLogs.logs(UUID.randomUUID()).futureValue
      response.isEmpty must be(true)
    }
  }
}
