package net.wiringbits.repositories

import akka.actor.ActorSystem
import akka.stream.scaladsl.*
import net.wiringbits.common.models.enums.{BackgroundJobStatus, BackgroundJobType}
import net.wiringbits.common.models.{Email, InstantCustom, UUIDCustom}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.models.jobs.BackgroundJobPayload
import net.wiringbits.typo_generated.customtypes.TypoJsonb
import net.wiringbits.typo_generated.public.background_jobs.BackgroundJobsRow
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.matchers.must.Matchers.*
import play.api.libs.json.{JsValue, Json}

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
        `type` = BackgroundJobType.SendEmail,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending,
        statusDetails = None,
        errorCount = Some(0),
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      repositories.backgroundJobs.create(createRequest).futureValue

      val result = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
        .runWith(Sink.seq)
        .futureValue

      result.size must be(1)
      val item = result.headOption.value
      item.status must be(createRequest.status)
      item.`type` must be(createRequest.`type`)
      val itemJsValue = Json.parse(item.payload.value)
      val backgroundJobPayloadJsValue = Json.toJson(backgroundJobPayload)
      (itemJsValue \ "email") must be(backgroundJobPayloadJsValue \ "email")
      (itemJsValue \ "subject") must be(backgroundJobPayloadJsValue \ "subject")
      (itemJsValue \ "body") must be(backgroundJobPayloadJsValue \ "body")
    }

    "only return pending jobs" in withRepositories() { repositories =>
      val createRequestBase = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending,
        statusDetails = None,
        errorCount = Some(0),
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      val limit = 6
      for (i <- 1 to limit) {
        repositories.backgroundJobs
          .create(
            createRequestBase.copy(
              backgroundJobId = UUIDCustom.randomUUID(),
              status = if ((i % 2) == 0) BackgroundJobStatus.Success else BackgroundJobStatus.Pending
            )
          )
          .futureValue
      }
      val response = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
        .runWith(Sink.seq)
        .futureValue
      response.length must be(limit / 2)
      val backgroundJobPayloadJsValue = Json.toJson(backgroundJobPayload)
      response.foreach { x =>
        x.status must be(BackgroundJobStatus.Pending)
        x.`type` must be(createRequestBase.`type`)
        val itemJsValue = Json.parse(x.payload.value)
        (itemJsValue \ "email") must be(backgroundJobPayloadJsValue \ "email")
        (itemJsValue \ "subject") must be(backgroundJobPayloadJsValue \ "subject")
        (itemJsValue \ "body") must be(backgroundJobPayloadJsValue \ "body")
      }
    }

    "return no results" in withRepositories() { repositories =>
      val response = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
        .runWith(Sink.seq)
        .futureValue
      response.isEmpty must be(true)
    }
  }

  "setStatusToFailed" should {
    "work" in withRepositories() { repositories =>
      val createRequest = BackgroundJobsRow(
        backgroundJobId = UUIDCustom.randomUUID(),
        `type` = BackgroundJobType.SendEmail,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending,
        statusDetails = None,
        errorCount = Some(0),
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      repositories.backgroundJobs.create(createRequest).futureValue

      val failReason = "test"
      repositories.backgroundJobs
        .setStatusToFailed(createRequest.backgroundJobId, executeAt = InstantCustom.now(), failReason = failReason)
        .futureValue
      val result = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
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
        `type` = BackgroundJobType.SendEmail,
        payload = TypoJsonb(Json.toJson(backgroundJobPayload).toString),
        status = BackgroundJobStatus.Pending,
        statusDetails = None,
        errorCount = Some(0),
        executeAt = InstantCustom.now(),
        createdAt = InstantCustom.now(),
        updatedAt = InstantCustom.now()
      )

      repositories.backgroundJobs.create(createRequest).futureValue
      repositories.backgroundJobs.setStatusToSuccess(createRequest.backgroundJobId).futureValue

      val result = repositories.backgroundJobs
        .streamPendingJobs()
        .futureValue
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
