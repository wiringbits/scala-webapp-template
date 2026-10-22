package net.wiringbits.core

import com.dimafeng.testcontainers.PostgreSQLContainer
import org.slf4j.LoggerFactory
import org.testcontainers.utility.DockerImageName
import play.api.db.evolutions.Evolutions
import play.api.db.{Database, Databases}

import java.sql.DriverManager

/** A single Postgres container shared by every DB-backed spec in the test JVM.
  *
  * It is started lazily on first use, gets CITEXT and the evolutions applied once, and is never stopped per suite.
  * Tests stay isolated by using random unique data (see `utils.RandomData`) and user-scoped assertions.
  */
object SharedPostgres {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private lazy val instance: (PostgreSQLContainer, Database) = {
    val container = PostgreSQLContainer.Def(dockerImageName = DockerImageName.parse("postgres:13")).start()
    logger.info(s"Started shared Postgres container at ${container.jdbcUrl}")

    val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
    try conn.createStatement().execute("CREATE EXTENSION IF NOT EXISTS CITEXT;")
    finally conn.close()

    val database = Databases(
      driver = "org.postgresql.Driver",
      url = container.jdbcUrl,
      name = "default",
      config = Map(
        "username" -> container.username,
        "password" -> container.password
      )
    )
    Evolutions.applyEvolutions(database)

    sys.addShutdownHook {
      database.shutdown()
      container.stop()
    }

    (container, database)
  }

  def container: PostgreSQLContainer = instance._1

  def database: Database = instance._2
}
