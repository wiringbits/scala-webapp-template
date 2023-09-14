package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.{RepositoryComponents, RepositorySpec}
import net.wiringbits.repositories.models.{User, UserToken, UserTokenType}
import net.wiringbits.typo_generated.public.user_tokens.UserTokensRow

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.RepositoryUtils

import java.time.{Instant, OffsetDateTime}
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.concurrent.Future

class UserTokensRepositorySpec extends RepositorySpec with RepositoryUtils {
  private def createTokenRequest(
      userId: UUID,
      token: String = "test",
      userTokenType: UserTokenType = UserTokenType.ResetPassword
  )(using
      repositories: RepositoryComponents
  ): Future[UserTokensRow] = {
    val userTokensRow = UserTokensRow(
      userTokenId = UUID.randomUUID(),
      token = token,
      tokenType = userTokenType.toString,
      createdAt = Instant.now,
      expiresAt = Instant.now().plus(2L, ChronoUnit.DAYS),
      userId = userId
    )

    for {
      _ <- repositories.userTokens.create(userTokensRow)
    } yield userTokensRow
  }

  "create" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      createTokenRequest(request.userId).futureValue
    }

    "fail when the user doesn't exists" in withRepositories() { implicit repositories =>
      val ex = intercept[RuntimeException] {
        createTokenRequest(UUID.randomUUID()).futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_tokens" violates foreign key constraint "user_tokens_user_id_fk""""
      )
    }
  }

  "find(userId)" should {
    "return the user token" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val tokenRequest = createTokenRequest(request.userId).futureValue
      println(tokenRequest)

      val maybe = repositories.userTokens.find(request.userId).futureValue
      println(maybe.mkString("\n"))
      val response = maybe.headOption.value
      response.token must be(tokenRequest.token)
      response.tokenType must be(tokenRequest.tokenType)
      response.userTokenId must be(tokenRequest.userTokenId)
    }

    "return no results when the user doesn't exists" in withRepositories() { repositories =>
      val response = repositories.userTokens.find(UUID.randomUUID()).futureValue
      response.isEmpty must be(true)
    }
  }

  "find(userId, token)" should {
    "return the user token" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue
      val tokenRequest = createTokenRequest(request.userId).futureValue

      val response = repositories.userTokens.find(request.userId, tokenRequest.token).futureValue
      response.isDefined must be(true)
    }

    "return no results when the user doesn't exists" in withRepositories() { repositories =>
      val response = repositories.userTokens.find(UUID.randomUUID(), "test").futureValue
      response.isEmpty must be(true)
    }
  }

  "delete" should {
    "work" in withRepositories() { implicit repositories =>
      val request = createNonVerifyUser().futureValue

      val maybe = repositories.userTokens.find(request.userId).futureValue
      val tokenId = maybe.headOption.value.userTokenId

      repositories.userTokens.delete(userTokenId = tokenId, userId = request.userId).futureValue

      val response = repositories.userTokens.find(request.userId).futureValue
      response.isEmpty must be(true)
    }
  }
}
