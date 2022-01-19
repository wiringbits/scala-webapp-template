package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.common.models.Captcha
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.{mock, when}
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import net.wiringbits.api.models.{CreateUser, Login}
import net.wiringbits.common.models.{Email, Name, Password}

import scala.concurrent.Future
import scala.util.control.NonFatal

class UsersControllerSpec extends PlayPostgresSpec {

  private val captchaApi = mock[ReCaptchaApi]
  when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[ReCaptchaApi].to(captchaApi)
      )

  "POST /users" should {
    "return the authentication token after creating a user" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test@wiringbits.net")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val response = client.createUser(request).futureValue
      response.name must be(name)
      response.email must be(email)
      response.token mustNot be(empty)
    }

    "fail when the email is already taken" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("someone"),
        email = Email.trusted("test@wiringbits.net"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

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

    "fail when the captcha isn't valid" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(false))

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("Invalid captcha, try again")

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))
    }

    "fail when the email has a wrong format" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("someone"),
        email = Email.trusted("test1@email.@"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

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
      val request = CreateUser.Request(
        name = Name.trusted("someone"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123"),
        captcha = Captcha.trusted("test")
      )

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
      val request = CreateUser.Request(
        name = Name.trusted("n"),
        email = Email.trusted("test2@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

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

  "POST /users/login" should {
    "return the response from a correct user" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      val loginRequest = Login.Request(
        email = user.email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val loginResponse = client.login(loginRequest).futureValue
      loginResponse.name must be(user.name)
      loginResponse.email must be(user.email)
    }

    "fail when password is incorrect" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      val loginRequest = Login.Request(
        email = user.email,
        password = Password.trusted("Incorrect password"),
        captcha = Captcha.trusted("test")
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
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      client.createUser(request).futureValue

      val loginRequest = Login.Request(
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(false))

      val error = client
        .login(loginRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("Invalid captcha, try again")

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))
    }
  }
}
