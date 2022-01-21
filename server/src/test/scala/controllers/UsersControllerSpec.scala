package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUser, ForgotPassword, Login, ResetPassword, VerifyEmail}
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.common.models.*
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.MockitoSugar.{mock, when}
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import scala.concurrent.Future
import scala.util.control.NonFatal

class UsersControllerSpec extends PlayPostgresSpec with LoginUtils {

  private val emailApi = mock[EmailApi]
  when(emailApi.sendEmail(any[EmailRequest]())).thenReturn(Future.unit)

  private val captchaApi = mock[ReCaptchaApi]
  when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[EmailApi].to(emailApi),
        inject.bind[ReCaptchaApi].to(captchaApi)
      )

  "POST /users" should {
    "return the email verification after creating a user" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val response = client.createUser(request).futureValue

      response.name must be(name)
      response.email must be(email)
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
  }

  "POST /users/verify-email" should {
    "success on verifying user's email" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val response = createVerifyLoginUser(request, client).futureValue

      response.name must be(name)
      response.email must be(email)
    }

    "fail when trying to verify an already verified user's email" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val response = createVerifyLoginUser(request, client).futureValue

      val error = client
        .verifyEmail(VerifyEmail.Request(UserToken(response.id)))
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
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id))).futureValue

      val loginRequest = Login.Request(
        email = user.email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val loginResponse = client.login(loginRequest).futureValue
      loginResponse.name must be(user.name)
      loginResponse.email must be(user.email)
    }

    "fail when the user tries to login without an email verification" in withApiClient { client =>
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

      val error = client
        .login(loginRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("The email is not verified, check your spam folder if you don't see the email.")
    }

    "login after successful email confirmation" in withApiClient { client =>
      val email = Email.trusted("test1@email.com")
      val password = Password.trusted("test123...")
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = email,
        password = password,
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id))).futureValue

      val response =
        client.login(Login.Request(email = email, password = password, captcha = Captcha.trusted("test"))).futureValue
      response.email must be(email)
      response.token mustNot be(empty)
    }

    "fail when password is incorrect" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id))).futureValue

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

    "fail when user isn't email confirmed" in withApiClient { client =>
      val password = Password.trusted("test123...")
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = password,
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      val loginRequest = Login.Request(
        email = user.email,
        password = password,
        captcha = Captcha.trusted("test")
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

  }

  "POST /forgot-password" should {
    "create the reset password token after the user's request to reset their password" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      createVerifyLoginUser(request, client).futureValue

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      val response = client.forgotPassword(forgotPasswordRequest).futureValue

      response must be(ForgotPassword.Response())
    }

    "fail when the user tries to reset a password for nonexistent email" in withApiClient { client =>
      val email = Email.trusted("test@email.com")
      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))

      val error = client
        .forgotPassword(forgotPasswordRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"Email not registered")
    }

    "fail when the user tries to reset a password without their email verification step" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      client.createUser(request).futureValue

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))

      val error = client
        .forgotPassword(forgotPasswordRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"User's $email hasn't been verified yet")
    }

    "fail when the captcha isn't valid" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      createVerifyLoginUser(request, client).futureValue

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(false))

      val error = client
        .forgotPassword(forgotPasswordRequest)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be("Invalid captcha, try again")

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))
    }
  }

  "POST /reset-password" should {
    "reset a password for a given user" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val response = createVerifyLoginUser(request, client).futureValue

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      client.forgotPassword(forgotPasswordRequest).futureValue

      val resetPasswordRequest = ResetPassword.Request(UserToken(response.id), Password.trusted("test456..."))
      client.resetPassword(resetPasswordRequest).futureValue

      val loginRequest = Login.Request(
        email = request.email,
        password = Password.trusted("test456..."),
        captcha = Captcha.trusted("test")
      )
      val loginResponse = client.login(loginRequest).futureValue

      loginResponse.name must be(request.name)
      loginResponse.email must be(request.email)
    }

    "return a token when a user tries to reset a password" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val userId = createVerifyLoginUser(request, client).futureValue.id

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      client.forgotPassword(forgotPasswordRequest).futureValue

      val resetPasswordRequest =
        ResetPassword.Request(UserToken(userId), Password.trusted("test456..."))

      val response = client
        .resetPassword(resetPasswordRequest)
        .futureValue

      response must be(ResetPassword.Response(anyString()))
    }

    "ignore the request when the user tries to reset a password for unknown email" in withApiClient { client =>
      val email = Email.trusted("test2@email.com")
      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))

      val response2 = client.forgotPassword(forgotPasswordRequest).futureValue

      response2 must be(ForgotPassword.Response())
    }

    "fail when the user tries to login with their old password after the password resetting" in withApiClient {
      client =>
        val name = Name.trusted("wiringbits")
        val email = Email.trusted("test1@email.com")
        val request = CreateUser.Request(
          name = name,
          email = email,
          password = Password.trusted("test123..."),
          captcha = Captcha.trusted("test")
        )

        val response = createVerifyLoginUser(request, client).futureValue

        val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
        client.forgotPassword(forgotPasswordRequest).futureValue

        val resetPasswordRequest =
          ResetPassword.Request(UserToken(response.id), Password.trusted("test456..."))
        client.resetPassword(resetPasswordRequest).futureValue

        val loginRequest = Login.Request(
          email = request.email,
          password = Password.trusted("test123..."),
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
  }
}
