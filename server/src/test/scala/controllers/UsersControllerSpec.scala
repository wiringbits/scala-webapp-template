package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUserRequest, LoginRequest, VerifyEmailRequest}
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.{mock, when}
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import java.time.{Clock, Instant}
import scala.concurrent.Future
import scala.util.control.NonFatal

class UsersControllerSpec extends PlayPostgresSpec with LoginUtils {

  private val emailApi = mock[EmailApi]
  when(emailApi.sendEmail(any[EmailRequest]())).thenReturn(Future.unit)

  private val clock = mock[Clock]
  when(clock.instant()).thenReturn(Instant.now())

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[EmailApi].to(emailApi),
        inject.bind[Clock].to(clock)
      )

  "POST /users" should {
    "return the email verification after creating a user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = CreateUserRequest(name = name, email = email, password = "test123...")

      val response = client.createUser(request).futureValue

      response.name must be(name)
      response.email must be(email)
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
    "login after successful email confirmation" in withApiClient { client =>
      val email = "test1@email.com"
      val password = "test123..."
      val request = CreateUserRequest(
        name = "wiringbits",
        email = email,
        password = password
      )
      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmailRequest(user.id.toString)).futureValue

      val response = client.login(LoginRequest(email = email, password = password)).futureValue
      response.email must be(email)
      response.token mustNot be(empty)
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

    "fail when trying to verify an already verified user's email" in withApiClient { client =>
      val request = CreateUserRequest(
        name = "wiringbits",
        email = "test1@email.com",
        password = "test123..."
      )
      val response = createVerifyLoginUser(request, client).futureValue

      val error = client
        .verifyEmail(VerifyEmailRequest(response.id.toString))
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

      client.verifyEmail(VerifyEmailRequest(user.id.toString)).futureValue

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

      client.verifyEmail(VerifyEmailRequest(user.id.toString)).futureValue

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
  }
}
