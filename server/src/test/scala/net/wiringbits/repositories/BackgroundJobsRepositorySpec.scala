package net.wiringbits.repositories

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.*
import net.wiringbits.common.models.Email
import net.wiringbits.core.RepositorySpec
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.repositories.daos.{BackgroundJobDAO, backgroundJobParser}
import net.wiringbits.repositories.models.BackgroundJobData
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import play.api.libs.json.Json
import utils.RepositoryUtils

import java.time.Instant
import java.util.UUID

class BackgroundJobsRepositorySpec extends RepositorySpec with BeforeAndAfterAll with RepositoryUtils {

  // required to test the streaming operations
  private implicit lazy val system: ActorSystem = ActorSystem("BackgroundJobsRepositorySpec")

  override def afterAll(): Unit = {
    system.terminate().futureValue
    super.afterAll()
  }

  "streamPendingJobs" should {

    "work (simple case)" in withRepositories() { implicit repositories =>
      val createRequest = createBackgroundJobData()

      val result = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue

      result.size must be(1)
      val item = result.headOption.value
      item.status must be(createRequest.status)
      item.`type` must be(createRequest.`type`)
      item.payload must be(Json.toJson(createRequest.payload))
    }

    "only return pending jobs" in withRepositories() { implicit repositories =>
      val backgroundJobType = BackgroundJobType.SendEmail
      val payload = backgroundJobPayload
      val limit = 6
      for (i <- 1 to limit) {
        createBackgroundJobData(
          backgroundJobType = backgroundJobType,
          payload = payload,
          status = if (i % 2) == 0 then BackgroundJobStatus.Success else BackgroundJobStatus.Pending
        )
      }
      val response = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(limit / 2)
      response.foreach { x =>
        x.status must be(BackgroundJobStatus.Pending)
        x.`type` must be(backgroundJobType)
        x.payload must be(Json.toJson(payload))
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
    "work" in withRepositories() { implicit repositories =>
      val createRequest = createBackgroundJobData()

      val failReason = "test"
      repositories.backgroundJobs
        .setStatusToFailed(createRequest.id, executeAt = Instant.now(), failReason = failReason)
        .futureValue
      val result = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue

      result.size must be(1)
      val item = result.headOption.value
      item.id must be(createRequest.id)
      item.status must be(BackgroundJobStatus.Failed)
      item.statusDetails must be(Some(failReason))
    }

    "fail if the job doesn't exists" in withRepositories() { repositories =>
      pending // TODO: setStatusToFailed must actually return an error because right now it succeeds

      repositories.backgroundJobs
        .setStatusToFailed(UUID.randomUUID(), executeAt = Instant.now(), failReason = "test")
        .futureValue
    }
  }

  "setStatusToSuccess" should {
    "work" in withRepositories() { implicit repositories =>
      val createRequest = createBackgroundJobData()

      repositories.backgroundJobs.setStatusToSuccess(createRequest.id).futureValue

      val result = repositories.backgroundJobs.streamPendingJobs.futureValue
        .runWith(Sink.seq)
        .futureValue
      result.isEmpty must be(true)
    }

    "fail if the notification doesn't exists" in withRepositories() { repositories =>
      pending // TODO: setStatusToFailed must actually return an error because right now it succeeds
      repositories.backgroundJobs.setStatusToSuccess(UUID.randomUUID()).futureValue
    }
  }
}
