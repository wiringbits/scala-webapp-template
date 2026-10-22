package net.wiringbits.repositories

import net.wiringbits.core.{IsolatedPostgresSpec, RepositoryComponents, RepositorySpec}
import net.wiringbits.models.jobs.{BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.daos.{BackgroundJobDAO, backgroundJobParser}
import net.wiringbits.repositories.models.BackgroundJobData
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import utils.{Executors, RepositoryUtils}

import java.time.temporal.ChronoUnit
import java.time.{Clock, Duration, Instant}
import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.duration.*

// The background_jobs queue is global (not user-scoped), so these assertions need an empty queue per test
// (withEmptyJobQueue). An isolated database keeps that per-test DELETE from racing other suites on the shared container.
class BackgroundJobsRepositorySpec extends RepositorySpec with RepositoryUtils with IsolatedPostgresSpec {

  "setStatusToFailed" should {
    "work" in withEmptyJobQueue { implicit repositories =>
      val createRequest = createBackgroundJobData()

      val failReason = "test"
      val executeAt = Instant.now().plus(5, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS)
      repositories.backgroundJobs
        .setStatusToFailed(createRequest.id, executeAt = executeAt, failReason = failReason)
        .futureValue

      val item = jobOf(createRequest.id)
      item.status must be(BackgroundJobStatus.Failed)
      item.errorCount must be(1)
      item.statusDetails must be(Some(failReason))
      item.executeAt must be(executeAt)
    }

    "fail if the job doesn't exist" in withEmptyJobQueue { repositories =>
      intercept[RuntimeException] {
        repositories.backgroundJobs
          .setStatusToFailed(UUID.randomUUID(), executeAt = Instant.now(), failReason = "test")
          .futureValue
      }
    }
  }

  "setStatusToSuccess" should {
    "work" in withEmptyJobQueue { implicit repositories =>
      val createRequest = createBackgroundJobData()

      repositories.backgroundJobs.setStatusToSuccess(createRequest.id).futureValue

      val result = repositories.backgroundJobs.claimJobs(createRequest.`type`, 1.minute, limit = 10).futureValue
      result.isEmpty must be(true)
    }

    "fail if the job doesn't exist" in withEmptyJobQueue { repositories =>
      intercept[RuntimeException] {
        repositories.backgroundJobs.setStatusToSuccess(UUID.randomUUID()).futureValue
      }
    }
  }

  "claimJobs" should {
    val jobType = BackgroundJobType.SendEmail
    val timeout = 5.minutes

    "claim a ready pending job, marking it running until now + timeout" in withEmptyJobQueue { implicit repositories =>
      val createRequest = createBackgroundJobData(backgroundJobType = jobType)

      val before = Instant.now()
      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue
      val after = Instant.now()

      result.map(_.id) must be(List(createRequest.id))
      val item = result.headOption.value
      item.status must be(BackgroundJobStatus.Running)
      item.errorCount must be(0)
      val deadline = item.deadline.value
      deadline.isBefore(before.plusMillis(timeout.toMillis).minusMillis(1)) must be(false)
      deadline.isAfter(after.plusMillis(timeout.toMillis)) must be(false)
    }

    "claim a failed job whose execute_at has passed" in withEmptyJobQueue { implicit repositories =>
      val createRequest = createBackgroundJobData(backgroundJobType = jobType)
      repositories.backgroundJobs
        .setStatusToFailed(createRequest.id, executeAt = Instant.now(), failReason = "test")
        .futureValue

      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue

      result.map(_.id) must be(List(createRequest.id))
      result.headOption.value.status must be(BackgroundJobStatus.Running)
      result.headOption.value.errorCount must be(1)
    }

    "not claim a job whose execute_at is in the future" in withEmptyJobQueue { implicit repositories =>
      val _ = createBackgroundJobData(
        backgroundJobType = jobType,
        executeAt = Instant.now().plus(1, ChronoUnit.HOURS)
      )

      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue

      result must be(empty)
    }

    "not claim a successful job" in withEmptyJobQueue { implicit repositories =>
      val _ = createBackgroundJobData(backgroundJobType = jobType, status = BackgroundJobStatus.Success)

      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue

      result must be(empty)
    }

    "not claim a failed job that reached the error limit" in withEmptyJobQueue { implicit repositories =>
      val exhausted = createBackgroundJobData(backgroundJobType = jobType)
      for (_ <- 1 to 10) {
        repositories.backgroundJobs
          .setStatusToFailed(exhausted.id, executeAt = Instant.now(), failReason = "test")
          .futureValue
      }
      errorCountOf(exhausted.id) must be(10)

      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue

      result must be(empty)
    }

    "claim at most limit jobs, oldest execute_at first" in withEmptyJobQueue { implicit repositories =>
      val now = Instant.now()
      val third = createBackgroundJobData(backgroundJobType = jobType, executeAt = now.minus(1, ChronoUnit.MINUTES))
      val first = createBackgroundJobData(backgroundJobType = jobType, executeAt = now.minus(3, ChronoUnit.MINUTES))
      val second = createBackgroundJobData(backgroundJobType = jobType, executeAt = now.minus(2, ChronoUnit.MINUTES))

      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 2).futureValue

      result.map(_.id).toSet must be(Set(first.id, second.id))
      val rest = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 2).futureValue
      rest.map(_.id) must be(List(third.id))
    }

    "not claim a running job before its deadline" in withEmptyJobQueue { implicit repositories =>
      val _ = createBackgroundJobData(backgroundJobType = jobType)
      repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue.size must be(1)

      val result = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue

      result must be(empty)
    }

    "reclaim a running job past its deadline, counting it as an error" in withEmptyJobQueue { implicit repositories =>
      val createRequest = createBackgroundJobData(backgroundJobType = jobType)
      repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue.size must be(1)

      val laterClock = Clock.offset(Clock.systemUTC, Duration.ofMillis(timeout.toMillis).plusMinutes(1))
      val laterRepository = new BackgroundJobsRepository(repositories.database)(using Executors.databaseEC, laterClock)
      val result = laterRepository.claimJobs(jobType, timeout, limit = 10).futureValue

      result.map(_.id) must be(List(createRequest.id))
      val item = result.headOption.value
      item.status must be(BackgroundJobStatus.Running)
      item.errorCount must be(1)
      item.deadline.value.isAfter(laterClock.instant()) must be(true)
    }

    "not reclaim an expired running job that reached the error limit" in withEmptyJobQueue { implicit repositories =>
      val createRequest = createBackgroundJobData(backgroundJobType = jobType)
      val step = Duration.ofMillis(timeout.toMillis).plusMinutes(1)
      def claimAt(n: Long) = {
        val clock = Clock.offset(Clock.systemUTC, step.multipliedBy(n))
        new BackgroundJobsRepository(repositories.database)(using Executors.databaseEC, clock)
          .claimJobs(jobType, timeout, limit = 10)
          .futureValue
      }

      claimAt(0).map(_.id) must be(List(createRequest.id))
      for (n <- 1L to 10L) {
        claimAt(n).map(_.errorCount) must be(List(n.toInt))
      }

      claimAt(11) must be(empty)
    }

    "skip jobs locked by a concurrent claim" in withEmptyJobQueue { implicit repositories =>
      val now = Instant.now()
      val jobs = (1 to 4).toList.map { i =>
        createBackgroundJobData(backgroundJobType = jobType, executeAt = now.minus(10L - i, ChronoUnit.MINUTES))
      }

      val (lockedIds, otherIds) = repositories.database.withTransaction { implicit conn =>
        given Clock = Clock.systemUTC
        val locked = BackgroundJobDAO.claimJobs(jobType, timeout, limit = 2)
        // the transaction holding the locks is still open
        val other = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 10).futureValue
        (locked.map(_.id), other.map(_.id))
      }

      lockedIds.toSet must be(jobs.take(2).map(_.id).toSet)
      otherIds.toSet must be(jobs.drop(2).map(_.id).toSet)
    }

    "give disjoint jobs to concurrent claims" in withEmptyJobQueue { implicit repositories =>
      val jobs = (1 to 20).toList.map(_ => createBackgroundJobData(backgroundJobType = jobType))

      val claims = (1 to 4).map(_ => repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 5))
      val claimed = Future
        .sequence(claims)(using implicitly, Executors.globalEC)
        .futureValue
        .flatten
        .map(_.id)
      val remaining = repositories.backgroundJobs.claimJobs(jobType, timeout, limit = 20).futureValue.map(_.id)

      val allClaimed = claimed ++ remaining
      allClaimed.distinct.size must be(allClaimed.size)
      allClaimed.toSet must be(jobs.map(_.id).toSet)
    }
  }

  private def jobOf(id: UUID)(using repositories: RepositoryComponents): BackgroundJobData =
    repositories.database.withConnection { implicit conn =>
      anorm
        .SQL("SELECT * FROM background_jobs WHERE background_job_id = {id}::UUID")
        .on("id" -> id.toString)
        .as(backgroundJobParser.single)
    }

  private def errorCountOf(id: UUID)(using repositories: RepositoryComponents): Int =
    repositories.database.withConnection { implicit conn =>
      anorm
        .SQL("SELECT error_count FROM background_jobs WHERE background_job_id = {id}::UUID")
        .on("id" -> id.toString)
        .as(anorm.SqlParser.scalar[Int].single)
    }
}
