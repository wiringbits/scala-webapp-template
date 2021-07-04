package controllers.common

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForEach
import org.testcontainers.utility.DockerImageName
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Environment, Mode}

trait PlayPostgresSpec extends PlayAPISpec with TestContainerForEach {
  private val postgresImage = DockerImageName.parse("postgres:13")
  override val containerDef: PostgreSQLContainer.Def = PostgreSQLContainer.Def(dockerImageName = postgresImage)

  /**
   * Loads configuration disabling evolutions on default database.
   *
   * This allows to not write a custom application.conf for testing
   * and ensure play evolutions are disabled.
   */
  private def loadConfigWithoutEvolutions(env: Environment, container: PostgreSQLContainer): Configuration = {
    val map = Map(
      "db.default.username" -> container.username,
      "db.default.password" -> container.password,
      "db.default.url" -> container.jdbcUrl
    )

    Configuration.from(map).withFallback(Configuration.load(env))
  }

  def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder =
    GuiceApplicationBuilder(loadConfiguration = env => loadConfigWithoutEvolutions(env, container))
      .in(Mode.Test)

  def withApplication[A](runTest: Application => A): A = {
    withContainers { postgres =>
      val app = guiceApplicationBuilder(postgres).build()
      runTest(app)
    }
  }
}
