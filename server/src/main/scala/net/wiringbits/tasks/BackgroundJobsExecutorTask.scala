package net.wiringbits.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject
import net.wiringbits.actions.internal.StreamPendingBackgroundJobsForeverAction
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.common.models.InstantCustom
import net.wiringbits.config.BackgroundJobsExecutorConfig
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobType}
import net.wiringbits.repositories.BackgroundJobsRepository
import net.wiringbits.repositories.models.BackgroundJobData
import net.wiringbits.typo_generated.public.background_jobs.BackgroundJobsRow
import net.wiringbits.util.{DelayGenerator, EmailMessage}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import java.time.{Clock, ZoneOffset}
import java.time.temporal.ChronoUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class BackgroundJobsExecutorTask @Inject() (
    config: BackgroundJobsExecutorConfig,
    streamPendingBackgroundJobsForeverAction: StreamPendingBackgroundJobsForeverAction,
    emailApi: EmailApi,
    backgroundJobsRepository: BackgroundJobsRepository
)(implicit
    ec: ExecutionContext,
    actorSystem: ActorSystem,
    clock: Clock
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting the background jobs executor task")
  actorSystem.scheduler.scheduleOnce(config.interval) {
    run()
  }

  private def execute(job: BackgroundJobsRow): Future[Unit] = {
    val executionResult = BackgroundJobType.withNameInsensitive(job.`type`) match {
      case BackgroundJobType.SendEmail =>
        Json.toJson(job.payload.value).asOpt[BackgroundJobPayload.SendEmail] match {
          case Some(typedPayload) => sendEmail(typedPayload)
          case None =>
            Future.failed(
              new RuntimeException(
                s"The given payload is not supported by the SendEmail task, please double check, job id = ${job.backgroundJobId}"
              )
            )
        }
    }

    executionResult
      .flatMap { _ =>
        backgroundJobsRepository.setStatusToSuccess(job.backgroundJobId)
      }
      .recoverWith { case NonFatal(ex) =>
        val minutesUntilExecute = DelayGenerator.createDelay(job.errorCount.getOrElse(0))
        val executeAt = InstantCustom.fromClock.plus(minutesUntilExecute, ChronoUnit.MINUTES)
        logger.warn(s"Job with id ${job.backgroundJobId} failed: ${ex.getMessage}", ex)
        backgroundJobsRepository.setStatusToFailed(job.backgroundJobId, executeAt, ex.getMessage)
      }
  }

  // TODO: Move to another file
  private def sendEmail(payload: BackgroundJobPayload.SendEmail): Future[Unit] = {
    val emailRequest = EmailRequest(payload.email, EmailMessage(subject = payload.subject, body = payload.body))
    emailApi.sendEmail(emailRequest)
  }

  def run(): Unit = {
    // TODO: Allow configuring the throttling mechanism
    // the reason to throttle and handle 1 background job concurrently is to avoid overloading the app
    val result = streamPendingBackgroundJobsForeverAction()
      .throttle(100, 1.minute)
      .runWith(akka.stream.scaladsl.Sink.foreachAsync(1)(execute))

    result.onComplete {
      case Failure(ex) =>
        logger.error(
          s"Failed to process pending background jobs, retrying after ${config.interval}: ${ex.getMessage}",
          ex
        )
        actorSystem.scheduler.scheduleOnce(config.interval) { run() }

      case Success(_) => actorSystem.scheduler.scheduleOnce(config.interval) { run() }
    }
  }
}
