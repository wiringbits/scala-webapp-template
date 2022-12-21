package net.wiringbits.core

import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.wordspec.AnyWordSpec
import utils.Executors

import java.time.Clock
import scala.concurrent.duration.DurationInt

trait RepositorySpec extends AnyWordSpec with PostgresSpec {
  implicit val patienceConfig: PatienceConfig = PatienceConfig(30.seconds, 1.second)

  def withRepositories[T](clock: Clock = Clock.systemUTC)(runTest: RepositoryComponents => T): T = withDatabase { db =>
    val users = new UsersRepository(db, UserTokensConfig(1.hour, 1.hour, "secret"))(Executors.databaseEC, clock)
    val userTokens = new UserTokensRepository(db)(Executors.databaseEC)
    val userLogs = new UserLogsRepository(db)(Executors.databaseEC)
    val backgroundJobs = new BackgroundJobsRepository(db)(Executors.databaseEC, clock)
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
}
