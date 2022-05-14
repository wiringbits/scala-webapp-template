package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{
  CreateUser,
  ForgotPassword,
  Login,
  ResetPassword,
  SendEmailVerificationToken,
  VerifyEmail
}
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.common.models._
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.UserTokenType
import net.wiringbits.util.{TokenGenerator, TokensHelper}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.{mock, when}
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.Future

class UsersControllerSpec extends PlayPostgresSpec with LoginUtils {

  def userTokensRepository: UserTokensRepository = app.injector.instanceOf(classOf[UserTokensRepository])

  private val clock = mock[Clock]
  when(clock.instant).thenReturn(Instant.now())

  private val tokenGenerator = mock[TokenGenerator]

  private val emailApi = mock[EmailApi]
  when(emailApi.sendEmail(any[EmailRequest]())).thenReturn(Future.unit)

  private val captchaApi = mock[ReCaptchaApi]
  when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))

  def userTokensConfig: UserTokensConfig = app.injector.instanceOf(classOf[UserTokensConfig])

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[EmailApi].to(emailApi),
        inject.bind[ReCaptchaApi].to(captchaApi),
        inject.bind[Clock].to(clock),
        inject.bind[TokenGenerator].to(tokenGenerator)
      )

  private def createHMACToken(token: UUID): String = {
    TokensHelper.doHMACSHA1(token.toString.getBytes, app.injector.instanceOf[UserTokensConfig].hmacSecret)
  }

  "POST /users" should {
    "return the email verification token after creating a user" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      val response = client.createUser(request).futureValue
      val token = userTokensRepository
        .find(response.id)
        .futureValue
        .find(_.tokenType == UserTokenType.EmailVerification)
        .value

      response.name must be(name)
      response.email must be(email)
      token.token must be(createHMACToken(verificationToken))
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
        .expectError
      error must be("The email is not available")
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
        .expectError

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
      val response = createVerifyLoginUser(request, client, tokenGenerator).futureValue

      response.name must be(name)
      response.email must be(email)
    }

    "delete the verification token after successful email confirmation" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(userId = user.id, token = verificationToken))).futureValue

      userTokensRepository.find(user.id).futureValue must be(empty)
    }

    "fail when trying to verify an already verified user's email" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = createVerifyLoginUser(request, client, tokenGenerator).futureValue

      val token = UUID.randomUUID()

      val error = client
        .verifyEmail(VerifyEmail.Request(UserToken(user.id, token)))
        .expectError

      error must be(s"User email is already verified")
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

      createVerifyLoginUser(request, client, tokenGenerator).futureValue

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      val response = client.forgotPassword(forgotPasswordRequest).futureValue

      response must be(ForgotPassword.Response())
    }

    "ignore the request when the user tries to reset a password for nonexistent email" in withApiClient { client =>
      val email = Email.trusted("test@email.com")
      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))

      val response = client.forgotPassword(forgotPasswordRequest).futureValue

      response must be(ForgotPassword.Response())
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
        .expectError

      error must be(s"The email is not verified, check your spam folder if you don't see the email.")
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

      createVerifyLoginUser(request, client, tokenGenerator).futureValue

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(false))

      val error = client
        .forgotPassword(forgotPasswordRequest)
        .expectError

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

      val user = createVerifyLoginUser(request, client, tokenGenerator).futureValue

      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      client.forgotPassword(forgotPasswordRequest).futureValue

      val resetPasswordRequest =
        ResetPassword.Request(UserToken(user.id, verificationToken), Password.trusted("test456..."))
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

    "return a email when a user tries to reset a password" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val userId = createVerifyLoginUser(request, client, tokenGenerator).futureValue.id

      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
      client.forgotPassword(forgotPasswordRequest).futureValue

      val resetPasswordRequest =
        ResetPassword.Request(UserToken(userId, verificationToken), Password.trusted("test456..."))

      val response = client
        .resetPassword(resetPasswordRequest)
        .futureValue

      response.email must be(email)
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

        val user = createVerifyLoginUser(request, client, tokenGenerator).futureValue

        val verificationToken = UUID.randomUUID()
        when(tokenGenerator.next()).thenReturn(verificationToken)

        val forgotPasswordRequest = ForgotPassword.Request(email, Captcha.trusted("test"))
        client.forgotPassword(forgotPasswordRequest).futureValue

        val resetPasswordRequest =
          ResetPassword.Request(UserToken(user.id, verificationToken), Password.trusted("test456..."))
        client.resetPassword(resetPasswordRequest).futureValue

        val loginRequest = Login.Request(
          email = request.email,
          password = Password.trusted("test123..."),
          captcha = Captcha.trusted("test")
        )

        val error = client
          .login(loginRequest)
          .expectError

        error must be("The given email/password doesn't match")
    }
  }

  "POST /users/email-verification-token" should {
    "success on send verifying token user's email" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = SendEmailVerificationToken.Request(
        email = email,
        captcha = Captcha.trusted("test")
      )

      val userRequest = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      val userCreated = client.createUser(userRequest).futureValue

      val response = client.sendEmailVerificationToken(request).futureValue

      val token = userTokensRepository
        .find(userCreated.id)
        .futureValue
        .find(_.tokenType == UserTokenType.EmailVerification)
        .value

      response.expiresAt must be(token.expiresAt)
    }

    "success on verifying email and login" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val captcha = Captcha.trusted("test")
      val password = Password.trusted("test123...")
      val request = SendEmailVerificationToken.Request(
        email = email,
        captcha = captcha
      )

      val userRequest = CreateUser.Request(
        name = name,
        email = email,
        password = password,
        captcha = captcha
      )

      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      val userCreated = client.createUser(userRequest).futureValue

      val emailVerificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(emailVerificationToken)

      client.sendEmailVerificationToken(request).futureValue
      client.verifyEmail(VerifyEmail.Request(UserToken(userCreated.id, emailVerificationToken))).futureValue

      val loginRequest = Login.Request(
        email = email,
        password = password,
        captcha = captcha
      )
      val loginResponse = client.login(loginRequest).futureValue

      loginResponse.name must be(userRequest.name)
      loginResponse.email must be(userRequest.email)
    }

    "fail when user's email is not registered" in withApiClient { client =>
      val email = Email.trusted("test1@email.com")
      val request = SendEmailVerificationToken.Request(
        email = email,
        captcha = Captcha.trusted("test")
      )
      val error = client.sendEmailVerificationToken(request).expectError

      error must be(s"The email is not registered")
    }

    "fail when the captcha isn't valid" in withApiClient { client =>
      val email = Email.trusted("test1@email.com")
      val request = SendEmailVerificationToken.Request(
        email = email,
        captcha = Captcha.trusted("test")
      )

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(false))

      val error = client
        .sendEmailVerificationToken(request)
        .expectError

      error must be("Invalid captcha, try again")

      when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))
    }

    "fail if the user is already verified" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test1@email.com")
      val request = SendEmailVerificationToken.Request(
        email = email,
        captcha = Captcha.trusted("test")
      )

      val userRequest = CreateUser.Request(
        name = name,
        email = email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val verificationToken = UUID.randomUUID()
      when(tokenGenerator.next()).thenReturn(verificationToken)

      createVerifyLoginUser(userRequest, client, tokenGenerator).futureValue

      val error = client
        .sendEmailVerificationToken(request)
        .expectError

      error must be(s"User email is already verified")
    }
  }
}
