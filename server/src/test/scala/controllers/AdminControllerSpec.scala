package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.CreateUser
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.common.models._
import net.wiringbits.util.TokenGenerator
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.{mock, when}
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.Future

class AdminControllerSpec extends PlayPostgresSpec with LoginUtils {

  private val tokenGenerator = mock[TokenGenerator]

  private val clock = mock[Clock]
  when(clock.instant()).thenReturn(Instant.now())

  private val emailApi = mock[EmailApi]
  when(emailApi.sendEmail(any[EmailRequest]())).thenReturn(Future.unit)

  private val captchaApi = mock[ReCaptchaApi]
  when(captchaApi.verify(any[Captcha]())).thenReturn(Future.successful(true))

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[TokenGenerator].to(tokenGenerator),
        inject.bind[EmailApi].to(emailApi),
        inject.bind[ReCaptchaApi].to(captchaApi),
        inject.bind[Clock].to(clock)
      )

  "GET /admin/users" should {
    "get every user" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )

      val list = List(1, 2, 3)
      list.foreach { i =>
        createVerifyLoginUser(
          request.copy(email = Email.trusted(s"test$i@email.com")),
          client,
          tokenGenerator
        ).futureValue
      }

      val response = client.adminGetUsers().futureValue
      response.data.length must be(list.length)
    }

    "return no results" in withApiClient { client =>
      val response = client.adminGetUsers().futureValue
      response.data.isEmpty must be(true)
    }
  }

  "GET /admin/users/:userId/logs" should {
    "get user logs" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("wiringbits"),
        email = Email.trusted("test@email.com"),
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      val user = createVerifyLoginUser(request, client, tokenGenerator).futureValue
      val response = client.adminGetUserLogs(user.id).futureValue
      response.data.isEmpty must be(false)
    }

    "return no results" in withApiClient { client =>
      val response = client.adminGetUserLogs(UUID.randomUUID()).futureValue
      response.data.isEmpty must be(true)
    }
  }
}
