package net.wiringbits.core

import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.wordspec.AnyWordSpec
import utils.Executors

import java.time.Clock
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

trait RepositorySpec extends AnyWordSpec with PostgresSpec {
  implicit val patienceConfig: PatienceConfig = PatienceConfig(30.seconds, 1.second)
  implicit val executionContext: ExecutionContext = Executors.globalEC

  def withRepositories[T](clock: Clock = Clock.systemUTC)(runTest: RepositoryComponents => T): T = withDatabase { db =>
    val users = new UsersRepository(db, UserTokensConfig(1.hour, 1.hour, "secret"))(using Executors.databaseEC, clock)
    val userTokens = new UserTokensRepository(db)(using Executors.databaseEC)
    val userLogs = new UserLogsRepository(db)(using Executors.databaseEC)
    val backgroundJobs = new BackgroundJobsRepository(db)(using Executors.databaseEC, clock)
    val components =
      RepositoryComponents(
        db,
        users,
        userTokens,
        userLogs,
        backgroundJobs
      )
    runTest(components)
  }

  /** background_jobs is a global queue that isn't scoped by user, so claim/stream semantics can only be tested against
    * an empty queue. Only the background job specs should use this, and they must mix in IsolatedPostgresSpec so the
    * DELETE can't race a concurrent suite on the shared container.
    */
  def withEmptyJobQueue[T](runTest: RepositoryComponents => T): T = withRepositories() { repositories =>
    repositories.database.withConnection { implicit conn =>
      anorm.SQL("DELETE FROM background_jobs").executeUpdate()
    }
    runTest(repositories)
  }
}
