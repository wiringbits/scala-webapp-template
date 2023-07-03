package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUser, Login, VerifyEmail}
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.common.models.*
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.util.TokenGenerator
//import org.scalatest.TestSuiteMixin

//import org.mockito.ArgumentMatchers.any
//import org.mockito.MockitoSugar.{mock, when}

import eu.monniot.scala3mock.matchers.MatchAny
import eu.monniot.scala3mock.macros.{mock, when}
import eu.monniot.scala3mock.main.withExpectations
import eu.monniot.scala3mock.scalatest.MockFactory

import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.Future

class AuthControllerSpec extends PlayPostgresSpec with LoginUtils with MockFactory {




  def userTokensRepository: UserTokensRepository = app.injector.instanceOf(classOf[UserTokensRepository])
  class Test{
    val clock = mock[Clock]
    when(()=>clock.instant).expects().returning(Instant.now())
  
    val tokenGenerator = mock[TokenGenerator]
  
    val emailApi = mock[EmailApi]
    when(()=>emailApi.sendEmail(mock[EmailRequest])).expects().returning(Future.unit)
  
    val captchaApi = mock[ReCaptchaApi]
    when(()=>captchaApi.verify(mock[Captcha])).expects().returning(Future.successful(true))
  }
  val test=Test()
  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[EmailApi].to(test.emailApi),
        inject.bind[ReCaptchaApi].to(test.captchaApi),
        inject.bind[Clock].to(test.clock),
        inject.bind[TokenGenerator].to(test.tokenGenerator)
      )

  def userTokensConfig: UserTokensConfig = app.injector.instanceOf(classOf[UserTokensConfig])

  "POST /auth/login" should {
    "return the response from a correct user" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val verificationToken = UUID.randomUUID()
      when(()=>test.tokenGenerator.next()).expects().returning(verificationToken)

      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken))).futureValue

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
        .expectError

      error must be("The email is not verified, check your spam folder if you don't see the email.")
    }

    "fail when the user tries to verify with a wrong token" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = client.createUser(request).futureValue

      val verificationToken = UUID.randomUUID()
      when(()=>test.tokenGenerator.next()).expects().returns(verificationToken)

      val error = client
        .verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken)))
        .expectError

      error must be(s"Token for user ${user.id} wasn't found")
    }

    "fail when the user tries to verify with an expired token" in withApiClient { client =>
      when(()=>test.clock.instant).expects().returning(Instant.now())
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val verificationToken = UUID.randomUUID()
      when(()=>test.tokenGenerator.next()).expects().returning(verificationToken)

      val user = client.createUser(request).futureValue

      when(()=>test.clock.instant).expects().returning(Instant.now().plus(2, ChronoUnit.DAYS))

      val error = client
        .verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken)))
        .expectError

      error must be("Token is expired")
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
      val verificationToken = UUID.randomUUID()
      when(()=>test.tokenGenerator.next()).expects().returns(verificationToken)

      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken))).futureValue

      val response =
        client.login(Login.Request(email = email, password = password, captcha = Captcha.trusted("test"))).futureValue
      response.email must be(email)
    }

    "fail when password is incorrect" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val verificationToken = UUID.randomUUID()
      when(()=>test.tokenGenerator.next()).expects().returns(verificationToken)


      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken))).futureValue

      val loginRequest = Login.Request(
        email = user.email,
        password = Password.trusted("Incorrect password"),
        captcha = Captcha.trusted("test")
      )

      val error = client
        .login(loginRequest)
        .expectError

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

      when(()=>test.captchaApi.verify(mock[Captcha])).expects().returns(Future.successful(false))

      val error = client
        .login(loginRequest)
        .expectError

      error must be("Invalid captcha, try again")

      when(()=>test.captchaApi.verify(mock[Captcha])).expects().returns(Future.successful(false))
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
        .expectError

      error must be("The email is not verified, check your spam folder if you don't see the email.")
    }
  }

  "GET /auth/me" should {
    "return current logged user" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val verificationToken = UUID.randomUUID()
      when(()=>test.tokenGenerator.next()).expects().returns(verificationToken)
      val user = client.createUser(request).futureValue

      client.verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken))).futureValue

      val loginRequest = Login.Request(
        email = Email.trusted("test1@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      client.login(loginRequest).futureValue

      val currentUser = client.currentUser().futureValue
      currentUser.id must be(user.id)
      currentUser.name must be(user.name)
      currentUser.email must be(user.email)
    }

    "fail if user isn't logged in" in withApiClient { client =>
      val error = client.currentUser().expectError
      error must be("Unauthorized: Invalid or missing authentication")
    }
  }
}
