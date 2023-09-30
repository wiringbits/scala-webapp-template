package net.wiringbits.repositories

import net.wiringbits.core.RepositorySpec
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.RepositoryUtils

import java.util.UUID

class UserTokensRepositorySpec extends RepositorySpec with RepositoryUtils {
  "create" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      createToken(request.id).futureValue
    }

    "fail when the user doesn't exists" in withRepositories() { implicit repositories =>
      val ex = intercept[RuntimeException] {
        createToken(UUID.randomUUID()).futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_tokens" violates foreign key constraint "user_tokens_user_id_fk""""
      )
    }
  }

  "find(userId)" should {
    "return the user token" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val tokenRequest = createToken(request.id).futureValue

      val maybe = repositories.userTokens.find(request.id).futureValue
      val response = maybe.headOption.value
      response.token must be(tokenRequest.token)
      response.tokenType must be(tokenRequest.tokenType)
      response.id must be(tokenRequest.id)
    }

    "return no results when the user doesn't exists" in withRepositories() { repositories =>
      val response = repositories.userTokens.find(UUID.randomUUID()).futureValue
      response.isEmpty must be(true)
    }
  }

  "find(userId, token)" should {
    "return the user token" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val tokenRequest = createToken(request.id).futureValue

      val response = repositories.userTokens.find(request.id, tokenRequest.token).futureValue
      response.isDefined must be(true)
    }

    "return no results when the user doesn't exists" in withRepositories() { repositories =>
      val response = repositories.userTokens.find(UUID.randomUUID(), "test").futureValue
      response.isEmpty must be(true)
    }
  }

  "delete" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createUser().futureValue

      val maybe = repositories.userTokens.find(request.id).futureValue
      val tokenId = maybe.headOption.value.id

      repositories.userTokens.delete(tokenId = tokenId, userId = request.id).futureValue

      val response = repositories.userTokens.find(request.id).futureValue
      response.isEmpty must be(true)
    }
  }
}
