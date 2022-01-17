package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUserRequest, LoginRequest, VerifyEmailRequest}
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.{EmailRequest, TokenType}
import net.wiringbits.repositories.TokensRepository
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.{doNothing, mock, when}
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import java.time.{Clock, Instant}
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.util.control.NonFatal

class UsersControllerSpec extends PlayPostgresSpec with LoginUtils {

  def tokensRepository = app.injector.instanceOf(classOf[TokensRepository])

  private val emailApi = mock[EmailApi]
  doNothing.when(emailApi).sendEmail(any[EmailRequest]())

  private val clock = mock[Clock]
  when(clock.instant()).thenReturn(Instant.now())

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[EmailApi].to(emailApi)
      )

  "POST /users" should {
    "return the email verification after creating a user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = CreateUserRequest(name = name, email = email, password = "test123...")

      val response = client.createUser(request).futureValue
      val token = tokensRepository.find(response.id).futureValue.headOption

      response.name must be(name)
      response.email must be(email)
      token mustNot be(empty)
      token.value.tokenType must be(TokenType.VerificationToken)
    }

    "fail when the email is already taken" in withApiClient { client =>
      val request = CreateUserRequest(name = "someone", email = "test@wiringbits.net", password = "test123...")

      // take the email
      client.createUser(request).futureValue

      // then, it fails
      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("Email already in use, pick another one")
    }

    "fail when the email has a wrong format" in withApiClient { client =>
      val request = CreateUserRequest(name = "someone", email = "test1@email.@", password = "test123...")

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("Invalid email address")
    }

    "fail when the password is too short" in withApiClient { client =>
      val request = CreateUserRequest(name = "someone", email = "test1@email.com", password = "test123")

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("The password must contain at least 8 characters")
    }

    "fail when the name is too short" in withApiClient { client =>
      val request = CreateUserRequest(name = "n", email = "test2@email.com", password = "test123...")

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("The name must contain at least 2 characters")
    }
  }

  "POST /users/verify-email" should {
    "delete the verification token after successful email confirmation" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val user = client.createUser(request).futureValue

      val token = tokensRepository.find(user.id).futureValue.head.token.toString
      val userIdToken = user.id.toString + "_" + token

      client.verifyEmail(VerifyEmailRequest(userIdToken)).futureValue

      tokensRepository.find(user.id).futureValue must be(empty)
    }

    "fail when the user tries to login without an email verification" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val user = client.createUser(request).futureValue

      val loginRequest = LoginRequest(
        email = user.email,
        password = "test123..."
      )

      val error = client
        .login(loginRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("The email is not verified, check your spam folder if you don't see the email.")
    }

    "fail when the user tries to verify with a wrong token" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val user = client.createUser(request).futureValue

      val token = UUID.randomUUID.toString
      val userIdToken = user.id.toString + "_" + token

      val error = client
        .verifyEmail(VerifyEmailRequest(userIdToken))
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"Token for user ${user.id} wasn't found")
    }

    "fail when the user tries to verify with an expired token" in withApiClient { client =>
      when(clock.instant()).thenAnswer(Instant.now())
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val user = client.createUser(request).futureValue

      val token = tokensRepository.find(user.id).futureValue.head.token.toString
      val userIdToken = user.id.toString + "_" + token

      when(clock.instant()).thenAnswer(Instant.now().plus(2, ChronoUnit.DAYS))

      val error = client
        .verifyEmail(VerifyEmailRequest(userIdToken))
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("Token is expired")
    }

    "fail when trying to verify an already verified user's email" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val response = createVerifyLoginUser(request, client, tokensRepository).futureValue

      val token = UUID.randomUUID()
      val userIdToken = response.id.toString + "_" + token

      val error = client
        .verifyEmail(VerifyEmailRequest(userIdToken))
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"User ${response.id} email is already verified")
    }
  }

  "POST /users/login" should {
    "return the response from a correct user" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val user = client.createUser(request).futureValue

      val token = tokensRepository.find(user.id).futureValue.head.token
      val userIdToken = user.id.toString + "_" + token
      client.verifyEmail(VerifyEmailRequest(userIdToken)).futureValue

      val loginRequest = LoginRequest(
        email = user.email,
        password = "test123..."
      )
      val loginResponse = client.login(loginRequest).futureValue
      loginResponse.name must be(user.name)
      loginResponse.email must be(user.email)
    }

    "fail when password is incorrect" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val user = client.createUser(request).futureValue

      val token = tokensRepository.find(user.id).futureValue.head.token
      val userIdToken = user.id.toString + "_" + token
      client.verifyEmail(VerifyEmailRequest(userIdToken)).futureValue

      val loginRequest = LoginRequest(
        email = user.email,
        password = "Incorrect password"
      )

      val error = client
        .login(loginRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("The given email/password doesn't match")
    }

    "fail when the captcha isn't valid" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      client.createUser(request).futureValue

      val loginRequest = LoginRequest(
        email = "test1@email.com",
        password = "test123..."
      )

      val error = client
        .login(loginRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("Invalid captcha, try again")
    }
  }
}
