package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUser, ForgotPassword, Login, ResetPassword, VerifyEmail, SendEmailVerificationToken}
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

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.Future

class UsersControllerSpec extends PlayPostgresSpec with LoginUtils {

  def userTokensRepository: UserTokensRepository = app.injector.instanceOf(classOf[UserTokensRepository])

  private val clock = mock[Clock]
  when(clock.instant()).thenReturn(Instant.now())

  private val tokenGenerator = mock[TokenGenerator]

  private val emailApi = mock[EmailApi]
  when(emailApi.sendEmail(any[EmailRequest]())).thenReturn(Future.unit)

  private val captchaApi = mock[ReCaptchaApi]
  when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))

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

  "POST /users/email-verification-token" should {
    "success on re send verifying user's email" in withApiClient { client =>
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

      client.createUser(userRequest).futureValue
      val response = client.sendEmailVerificationToken(request).futureValue
      //response.message must be(s"Email sent to verify.")
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
  }
}