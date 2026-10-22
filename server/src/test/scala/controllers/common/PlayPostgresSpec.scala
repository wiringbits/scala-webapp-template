package controllers.common

import com.dimafeng.testcontainers.PostgreSQLContainer
import net.wiringbits.api.ApiClient
import net.wiringbits.core.SharedPostgres
import net.wiringbits.services.{BcryptPasswordHasher, PasswordHasher}
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Environment, Mode}
import sttp.client3.HttpClientFutureBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait PlayPostgresSpec extends PlayAPISpec with GuiceOneServerPerSuite {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(30.seconds, 1.second)

  /** Loads configuration pointing the default database to the given container.
    *
    * This allows to not write a custom application.conf for testing. The shared container is already evolved, so Play's
    * startup evolutions find nothing to apply.
    */
  private def loadConfigForContainer(env: Environment, container: PostgreSQLContainer): Configuration = {
    val map = Map(
      "db.default.username" -> container.username,
      "db.default.password" -> container.password,
      "db.default.url" -> container.jdbcUrl
    )

    Configuration.from(map).withFallback(Configuration.load(env))
  }

  def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    GuiceApplicationBuilder(loadConfiguration = env => loadConfigForContainer(env, container))
      .in(Mode.Test)
      // Use the real bcrypt hasher with the cheapest cost so password hashing/verification doesn't dominate test time.
      .overrides(bind[PasswordHasher].toInstance(new BcryptPasswordHasher(logRounds = 4)))

  override def fakeApplication(): Application =
    guiceApplicationBuilder(SharedPostgres.container).build()

  def withApiClient[A](runTest: ApiClient => A): A = {
    implicit val sttpBackend: sttp.client3.SttpBackend[concurrent.Future, ?] = HttpClientFutureBackend()

    val config = ApiClient.Config(s"http://localhost:$port")
    val client = new ApiClient(config)
    runTest(client)
  }

  implicit class RichFutureExt[T](val future: Future[T]) {
    def expectError: String = {
      future
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
    }
  }
}
