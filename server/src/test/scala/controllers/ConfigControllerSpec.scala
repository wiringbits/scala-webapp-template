package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUser, ForgotPassword, Login, ResetPassword, VerifyEmail}
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

class ConfigControllerSpec extends PlayPostgresSpec with LoginUtils {

  private val clock = mock[Clock]
  when(clock.instant()).thenReturn(Instant.now())

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder(container)
      .overrides(
        inject.bind[Clock].to(clock)
      )

  private def createHMACToken(token: UUID): String = {
    TokensHelper.doHMACSHA1(token.toString.getBytes, app.injector.instanceOf[UserTokensConfig].hmacSecret)
  }

  "POST /config" should {
    "return the configuration" in withApiClient { client =>
      val response = client.getConfig().futureValue
      response.siteKey must be("6LeIxAcTAAAAAGG")
    }
  }
}
