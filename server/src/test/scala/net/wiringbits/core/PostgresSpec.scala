package net.wiringbits.core

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForEach
import org.scalatest.Suite
import org.testcontainers.utility.DockerImageName
import play.api.db.evolutions.Evolutions
import play.api.db.{Database, Databases}

import java.sql.DriverManager

trait PostgresSpec extends TestContainerForEach {
  self: Suite =>
  private val postgresImage = DockerImageName.parse("postgres:13")
  override val containerDef: PostgreSQLContainer.Def = PostgreSQLContainer.Def(dockerImageName = postgresImage)

  def initDatabase(postgres: Containers): Unit = {
    val conn = DriverManager.getConnection(
      postgres.container.getJdbcUrl,
      postgres.container.getUsername,
      postgres.container.getPassword
    )
    conn.createStatement().execute("CREATE EXTENSION CITEXT;")
    conn.close()
  }

  def withDatabase[T](runTest: Database => T): T = withContainers { postgres =>
    initDatabase(postgres)

    val database = Databases(
      driver = "org.postgresql.Driver",
      url = postgres.jdbcUrl,
      name = "default",
      config = Map(
        "username" -> postgres.container.getUsername,
        "password" -> postgres.container.getPassword
      )
    )

    Evolutions.applyEvolutions(database)

    runTest(database)
  }
}
