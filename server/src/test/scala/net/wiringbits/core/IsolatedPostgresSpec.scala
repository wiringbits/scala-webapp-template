package net.wiringbits.core

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.Suite
import org.testcontainers.utility.DockerImageName
import play.api.db.evolutions.Evolutions
import play.api.db.{Database, Databases}

import java.sql.DriverManager

/** Runs the suite against a Postgres container private to it, instead of the shared one in [[PostgresSpec]].
  *
  * Suites that touch a global, un-user-scoped table (e.g. the `background_jobs` queue) and assert over all of its rows
  * would race any concurrent suite once tests run in parallel, so they mix this in to get their own database.
  */
trait IsolatedPostgresSpec extends PostgresSpec with TestContainerForAll { self: Suite =>

  override val containerDef: PostgreSQLContainer.Def =
    PostgreSQLContainer.Def(DockerImageName.parse("postgres:13"))

  private var isolatedDatabase: Option[Database] = None

  override def afterContainersStart(containers: Containers): Unit = {
    super.afterContainersStart(containers)
    val conn =
      DriverManager.getConnection(
        containers.jdbcUrl,
        containers.container.getUsername,
        containers.container.getPassword
      )
    try conn.createStatement().execute("CREATE EXTENSION IF NOT EXISTS CITEXT;")
    finally conn.close()

    val database = Databases(
      driver = "org.postgresql.Driver",
      url = containers.jdbcUrl,
      name = "default",
      config = Map(
        "username" -> containers.container.getUsername,
        "password" -> containers.container.getPassword
      )
    )
    Evolutions.applyEvolutions(database)
    isolatedDatabase = Some(database)
  }

  override def beforeContainersStop(containers: Containers): Unit = {
    isolatedDatabase.foreach(_.shutdown())
    isolatedDatabase = None
    super.beforeContainersStop(containers)
  }

  override def withDatabase[T](runTest: Database => T): T =
    runTest(isolatedDatabase.getOrElse(sys.error("Isolated Postgres container not started")))
}
