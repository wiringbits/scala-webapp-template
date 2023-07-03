package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.CreateUser
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.common.models.*
import net.wiringbits.util.TokenGenerator
import org.scalatest.{OneInstancePerTest, TestSuiteMixin}
//import org.mockito.ArgumentMatchers.any
//import org.mockito.MockitoSugar.{mock, when}
import eu.monniot.scala3mock.matchers.MatchAny
import eu.monniot.scala3mock.macros.{mock, when}
import eu.monniot.scala3mock.main.withExpectations
import eu.monniot.scala3mock.context.MockContext
import org.scalatest.matchers.should.Matchers
import eu.monniot.scala3mock.scalatest.MockFactory
import eu.monniot.scala3mock.functions.MockFunctions.mockFunction

import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import utils.LoginUtils

import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.Future
import org.scalatest.BeforeAndAfterAll

class AdminControllerSpec extends PlayPostgresSpec with LoginUtils with MockFactory{
  class Test{
  val tokenGenerator = mock[TokenGenerator]

  val clock = mock[Clock]
  when(() => clock.instant()).expects().returning(Instant.now())

  val emailApi = mock[EmailApi]
  when(() => emailApi.sendEmail(mock[EmailRequest])).expects().returning(Future.unit)

  val captchaApi = mock[ReCaptchaApi]
  when(() => captchaApi.verify(mock[Captcha])).expects().returning(Future.successful(true))}
  val test=Test()

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[TokenGenerator].to(test.tokenGenerator),
        inject.bind[EmailApi].to(test.emailApi),
        inject.bind[ReCaptchaApi].to(test.captchaApi),
        inject.bind[Clock].to(test.clock)
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
          test.tokenGenerator
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
      val user = createVerifyLoginUser(request, client, test.tokenGenerator).futureValue
      val response = client.adminGetUserLogs(user.id).futureValue
      response.data.isEmpty must be(false)
    }

    "return no results" in withApiClient { client =>
      val response = client.adminGetUserLogs(UUID.randomUUID()).futureValue
      response.data.isEmpty must be(true)
    }
  }
}
