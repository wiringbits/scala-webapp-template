package net.wiringbits.repositories

import akka.actor.ActorSystem
import akka.stream.scaladsl.*
import net.wiringbits.common.models.{Email, InstantCustom, UUIDCustom}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.typo_generated.customtypes.TypoJsonb
import net.wiringbits.typo_generated.public.background_jobs.{BackgroundJobsRepoImpl, BackgroundJobsRow}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import play.api.libs.json.Json

import java.time.Instant
import java.util.UUID

class BackgroundJobsRepositorySpec extends RepositorySpec with BeforeAndAfterAll {

  // required to test the streaming operations
  private implicit lazy val system: ActorSystem = ActorSystem("BackgroundJobsRepositorySpec")

  override def afterAll(): Unit = {
    system.terminate().futureValue
    super.afterAll()
  }

  private val backgroundJobPayload =
    BackgroundJobPayload.SendEmail(Email.trusted("sample@wiringbits.net"), subject = "Test message", body = "it works")
  "streamPendingJobs" should {

    "work (simple case)" in withRepositories() { repositories =>
      val createRequest = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = None,
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      repositories.database.withConnection { implicit conn =>
        BackgroundJobsRepoImpl.insert(createRequest)
      }

      val result = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue

      result.size must be(1)
      val item = result.headOption.value
      item.status must be(createRequest.status)
      item.`type` must be(createRequest.`type`)
      item.payload must be(Json.toJson(createRequest.payload))
    }

    "only return pending jobs" in withRepositories() { repositories =>
      val createRequestBase = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = None,
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      val limit = 6
      for (i <- 1 to limit) {
        repositories.database.withConnection { implicit conn =>
          BackgroundJobsRepoImpl.insert(
            createRequestBase.copy(
              backgroundJobId = UUIDCustom.randomUUID(),
              status = if ((i % 2) == 0) BackgroundJobStatus.Success.toString else BackgroundJobStatus.Pending.toString
            )
          )
        }
      }
      val response = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(limit / 2)
      response.foreach { x =>
        x.status must be(BackgroundJobStatus.Pending)
        x.`type` must be(createRequestBase.`type`)
        x.payload must be(Json.toJson(createRequestBase.payload))
      }
    }

    "return no results" in withRepositories() { repositories =>
      val response = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.isEmpty must be(true)
    }
  }

  "setStatusToFailed" should {
    "work" in withRepositories() { repositories =>
      val createRequest = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = None,
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      repositories.database.withConnection { implicit conn =>
        BackgroundJobsRepoImpl.insert(createRequest)
      }
      val failReason = "test"
      repositories.backgroundJobs
        .setStatusToFailed(createRequest.backgroundJobId, executeAt = InstantCustom.now(), failReason = failReason)
        .futureValue
      val result = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue

      result.size must be(1)
      val item = result.headOption.value
      item.backgroundJobId must be(createRequest.backgroundJobId)
      item.status must be(BackgroundJobStatus.Failed)
      item.statusDetails must be(Some(failReason))
    }

    "fail if the job doesn't exists" in withRepositories() { repositories =>
      pending // TODO: setStatusToFailed must actually return an error because right now it succeeds

      repositories.backgroundJobs
        .setStatusToFailed(
          UUIDCustom.randomUUID(),
          executeAt = InstantCustom.now(),
          failReason = "test"
        )
        .futureValue
    }
  }

  "setStatusToSuccess" should {
    "work" in withRepositories() { repositories =>
      val createRequest = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail.toString,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending.toString,
        statusDetails = None,
        errorCount = None,
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      repositories.database.withConnection { implicit conn =>
        BackgroundJobsRepoImpl.insert(createRequest)
      }
      repositories.backgroundJobs.setStatusToSuccess(createRequest.backgroundJobId).futureValue

      val result = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue
      result.isEmpty must be(true)
    }

    "fail if the notification doesn't exists" in withRepositories() { repositories =>
      pending // TODO: setStatusToFailed must actually return an error because right now it succeeds
      repositories.backgroundJobs.setStatusToSuccess(UUIDCustom.randomUUID()).futureValue
    }
  }
}
