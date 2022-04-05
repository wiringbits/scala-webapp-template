package net.wiringbits.repositories

import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.repositories.models.{User, UserToken, UserTokenType}
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.must.Matchers._

import java.time.{Clock, Instant}
import java.time.temporal.ChronoUnit
import java.util.UUID

class UserTokensRepositorySpec extends RepositorySpec {

  private val clock = mock[Clock]
  when(clock.instant()).thenAnswer(Instant.now())

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

      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(2, ChronoUnit.DAYS),
          userId = request.id
        )
      repositories.userTokens.create(tokenRequest).futureValue
    }

    "fail when the user doesn't exists" in withRepositories() { repositories =>
      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(2, ChronoUnit.DAYS),
          userId = UUID.randomUUID()
        )
      val ex = intercept[RuntimeException] {
        repositories.userTokens.create(tokenRequest).futureValue
      }
      ex.getCause.getMessage must startWith(
        s"""ERROR: insert or update on table "user_tokens" violates foreign key constraint "user_tokens_user_id_fk""""
      )
    }
  }

  "find(userId)" should {
    "return the user token" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(2, ChronoUnit.DAYS),
          userId = request.id
        )
      repositories.userTokens.create(tokenRequest).futureValue

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

    "return no results if tokens are expired" in withRepositories(clock) { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(1, ChronoUnit.HOURS),
          userId = request.id
        )
      repositories.userTokens.create(tokenRequest).futureValue

      when(clock.instant()).thenAnswer(Instant.now().plus(2, ChronoUnit.HOURS))

      val response = repositories.userTokens.find(request.id).futureValue
      response.isEmpty must be(true)
    }
  }

  "find(userId, token)" should {
    "return the user token" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(2, ChronoUnit.DAYS),
          userId = request.id
        )
      repositories.userTokens.create(tokenRequest).futureValue

      val response = repositories.userTokens.find(request.id, tokenRequest.token).futureValue
      response.isDefined must be(true)
    }

    "return no results when the user doesn't exists" in withRepositories() { repositories =>
      val response = repositories.userTokens.find(UUID.randomUUID(), "test").futureValue
      response.isEmpty must be(true)
    }

    "return no results if tokens are expired" in withRepositories(clock) { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(1, ChronoUnit.HOURS),
          userId = request.id
        )
      repositories.userTokens.create(tokenRequest).futureValue

      when(clock.instant()).thenAnswer(Instant.now().plus(2, ChronoUnit.HOURS))

      val response = repositories.userTokens.find(request.id, tokenRequest.token).futureValue
      response.isEmpty must be(true)
    }
  }

  "getExpiredTokens" should {
    "return expired tokens" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val tokenRequest =
        UserToken.Create(
          id = UUID.randomUUID(),
          token = "test",
          tokenType = UserTokenType.ResetPassword,
          createdAt = Instant.now(),
          expiresAt = Instant.now.plus(1, ChronoUnit.HOURS),
          userId = request.id
        )
      repositories.userTokens.create(tokenRequest).futureValue

      when(clock.instant()).thenAnswer(Instant.now().plus(2, ChronoUnit.HOURS))

      val expiredUserTokens = repositories.userTokens.getExpiredTokens.futureValue

      // two tokens: creating an account and token created using tokenRequest
      expiredUserTokens.length must be(2)
    }

    "return no results" in withRepositories() { repositories =>
      val response = repositories.userTokens.getExpiredTokens.futureValue
      response.isEmpty must be(true)
    }
  }

  "delete" should {
    "work" in withRepositories() { repositories =>
      val request = User.CreateUser(
        id = UUID.randomUUID(),
        email = Email.trusted("hello@wiringbits.net"),
        name = Name.trusted("Sample"),
        hashedPassword = "password",
        verifyEmailToken = "token"
      )
      repositories.users.create(request).futureValue

      val maybe = repositories.userTokens.find(request.id).futureValue
      val tokenId = maybe.headOption.value.id

      repositories.userTokens.delete(tokenId = tokenId, userId = request.id).futureValue

      val response = repositories.userTokens.find(request.id).futureValue
      response.isEmpty must be(true)
    }
  }
}
