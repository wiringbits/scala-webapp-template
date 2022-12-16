package net.wiringbits.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject
import net.wiringbits.actions.internal.StreamPendingBackgroundJobsForeverAction
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.common.models.Email
import net.wiringbits.config.NotificationsConfig
import net.wiringbits.models.BackgroundJobType
import net.wiringbits.repositories.BackgroundJobsRepository
import net.wiringbits.repositories.models.BackgroundJobData
import net.wiringbits.util.{DelayGenerator, EmailMessage}
import org.slf4j.LoggerFactory
import play.api.libs.json.JsValue

import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class BackgroundJobsExecutorTask @Inject() (
    notificationsConfig: NotificationsConfig, // TODO: Update
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
  actorSystem.scheduler.scheduleOnce(
    notificationsConfig.interval // TODO: update
  ) {
    run()
  }

  private def execute(job: BackgroundJobData): Future[Unit] = {
    val executionResult = job.`type` match {
      case BackgroundJobType.SendEmail => sendEmail(job.id, job.payload)
      case BackgroundJobType.SendStatsToAdmin =>
        Future.failed(new RuntimeException("SendStatsToAdmin is not implemented yet"))
    }

    executionResult
      .flatMap { _ =>
        backgroundJobsRepository.setStatusToSuccess(job.id)
      }
      .recoverWith { case NonFatal(ex) =>
        val minutesUntilExecute = DelayGenerator.createDelay(job.errorCount)
        val executeAt = clock.instant().plus(minutesUntilExecute, ChronoUnit.MINUTES)
        logger.warn(s"Job with id ${job.id} failed: ${ex.getMessage}", ex)
        backgroundJobsRepository.setStatusToFailed(job.id, executeAt, ex.getMessage)
      }
  }

  // TODO: Move to another file
  private def sendEmail(jobId: UUID, payload: JsValue): Future[Unit] = {
    // TODO: Consider validating payloads before executing this method
    val emailRequestOpt = for {
      subject <- (payload \ "subject").asOpt[String]
      body <- (payload \ "body").asOpt[String]
      email <- (payload \ "email")
        .asOpt[String]
        .flatMap(str => Email.validate(str).toOption)
    } yield EmailRequest(email, EmailMessage(subject = subject, body = body))

    emailRequestOpt match {
      case Some(emailRequest) => emailApi.sendEmail(emailRequest)
      case None =>
        Future.failed(
          new RuntimeException(
            s"The given payload is not supported by the SendEmail task, please double check, job id = $jobId"
          )
        )
    }
  }

  def run(): Unit = {
    // TODO: Allow configuring the throttling mechanism
    // the reason to throttle and handle 1 background job concurrently is to avoid overloading the app
    val result = streamPendingBackgroundJobsForeverAction()
      .throttle(100, 1.minute)
      .runWith(akka.stream.scaladsl.Sink.foreachAsync(1)(execute))

    result.onComplete {
      case Failure(ex) =>
        // TODO: Update config
        logger.error(
          s"Failed to process pending background jobs, retrying after ${notificationsConfig.interval}: ${ex.getMessage}",
          ex
        )
        actorSystem.scheduler.scheduleOnce(notificationsConfig.interval) { run() }

      case Success(_) =>
        // TODO: Update config
        actorSystem.scheduler.scheduleOnce(notificationsConfig.interval) { run() }
    }
  }
}
