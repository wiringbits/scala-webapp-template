package net.wiringbits.tasks

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Sink
import com.google.inject.Inject
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.BackgroundJobsExecutorConfig
import net.wiringbits.models.jobs.{BackgroundJobPayload, BackgroundJobType}
import net.wiringbits.repositories.models.BackgroundJobData
import net.wiringbits.util.EmailMessage
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BackgroundJobsExecutorTask @Inject() (
    config: BackgroundJobsExecutorConfig,
    runner: BackgroundJobTypeRunner,
    emailApi: EmailApi
)(implicit
    ec: ExecutionContext,
    actorSystem: ActorSystem
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting the background jobs executor task")
  // resolved eagerly so a missing type fails at startup
  private val settingsPerType = BackgroundJobType.values.map(t => t -> config.settingsFor(t))
  settingsPerType.foreach { case (jobType, settings) =>
    actorSystem.scheduler.scheduleOnce(config.interval) {
      run(jobType, settings)
    }
  }

  private def dispatch(job: BackgroundJobData): Future[Unit] = {
    job.`type` match {
      case BackgroundJobType.SendEmail =>
        job.payload.asOpt[BackgroundJobPayload.SendEmail] match {
          case Some(typedPayload) => sendEmail(typedPayload)
          case None =>
            Future.failed(
              new RuntimeException(
                s"The given payload is not supported by the SendEmail task, please double check, job id = ${job.id}"
              )
            )
        }
    }
  }

  // TODO: Move to another file
  private def sendEmail(payload: BackgroundJobPayload.SendEmail): Future[Unit] = {
    val emailRequest = EmailRequest(payload.email, EmailMessage(subject = payload.subject, body = payload.body))
    emailApi.sendEmail(emailRequest)
  }

  private def run(jobType: BackgroundJobType, settings: BackgroundJobsExecutorConfig.TypeSettings): Unit = {
    val result = runner.source(jobType, settings, dispatch).runWith(Sink.ignore)

    result.onComplete {
      case Failure(ex) =>
        logger.error(
          s"Failed to process pending $jobType background jobs, retrying after ${config.interval}: ${ex.getMessage}",
          ex
        )
        actorSystem.scheduler.scheduleOnce(config.interval) { run(jobType, settings) }

      case Success(_) => actorSystem.scheduler.scheduleOnce(config.interval) { run(jobType, settings) }
    }
  }
}
