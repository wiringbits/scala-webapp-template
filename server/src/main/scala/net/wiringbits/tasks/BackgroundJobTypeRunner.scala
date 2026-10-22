package net.wiringbits.tasks

import com.google.inject.Inject
import net.wiringbits.config.BackgroundJobsExecutorConfig
import net.wiringbits.models.jobs.BackgroundJobType
import net.wiringbits.repositories.BackgroundJobsRepository
import net.wiringbits.repositories.models.BackgroundJobData
import net.wiringbits.util.DelayGenerator
import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after
import org.apache.pekko.stream.scaladsl.Source
import org.slf4j.LoggerFactory

import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeoutException
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/** Builds the processing stream for a single job type: claims jobs on demand (at most `parallelism` claimed or in
  * flight), runs them with a timeout and records the outcome.
  */
class BackgroundJobTypeRunner @Inject() (backgroundJobsRepository: BackgroundJobsRepository)(implicit
    ec: ExecutionContext,
    actorSystem: ActorSystem,
    clock: Clock
) {
  import BackgroundJobTypeRunner.*

  private val logger = LoggerFactory.getLogger(this.getClass)

  def source(
      jobType: BackgroundJobType,
      settings: BackgroundJobsExecutorConfig.TypeSettings,
      dispatch: BackgroundJobData => Future[Unit]
  ): Source[Unit, NotUsed] = {
    val visibility = settings.timeout + VisibilityMargin

    // Each pull performs a single claim attempt (followed by a poll delay when nothing was claimed), so the loop stops
    // as soon as the stream is cancelled and no job is claimed unless there is a free slot downstream.
    Source
      .unfoldAsync(())(_ => claimNext(jobType, visibility, settings.pollInterval).map(job => Some(((), job))))
      .collect { case Some(job) => job }
      // Unordered: an ordered mapAsync would let a slow job hold the slots of jobs completed after it
      .mapAsyncUnordered(settings.parallelism)(job => runOne(job, settings.timeout, dispatch))
  }

  private def claimNext(
      jobType: BackgroundJobType,
      visibility: FiniteDuration,
      pollInterval: FiniteDuration
  ): Future[Option[BackgroundJobData]] = {
    backgroundJobsRepository
      .claimJobs(jobType, visibility, limit = 1)
      .flatMap {
        case job :: _ => Future.successful(Some(job))
        case Nil => after(pollInterval)(Future.successful(None))
      }
      .recoverWith { case NonFatal(ex) =>
        logger.error(s"Failed to claim $jobType background jobs, retrying after $pollInterval: ${ex.getMessage}", ex)
        after(pollInterval)(Future.successful(None))
      }
  }

  private def runOne(
      job: BackgroundJobData,
      timeout: FiniteDuration,
      dispatch: BackgroundJobData => Future[Unit]
  ): Future[Unit] = {
    val dispatched = Future.delegate(dispatch(job))
    val timeoutException = new TimeoutException(s"Job ${job.id} (${job.`type`}) timed out after $timeout")
    val timedOut = after(timeout)(Future.failed[Unit](timeoutException))

    Future
      .firstCompletedOf(List(dispatched, timedOut))
      .flatMap { _ =>
        backgroundJobsRepository.setStatusToSuccess(job.id)
      }
      .recoverWith { case NonFatal(ex) =>
        if (ex eq timeoutException) logLateOutcome(job, dispatched)
        val minutesUntilExecute = DelayGenerator.createDelay(job.errorCount)
        val executeAt = clock.instant().plus(minutesUntilExecute, ChronoUnit.MINUTES)
        logger.warn(s"Job with id ${job.id} failed: ${ex.getMessage}", ex)
        backgroundJobsRepository.setStatusToFailed(job.id, executeAt, ex.getMessage)
      }
      .recover { case NonFatal(ex) =>
        // keep the stream alive, the claim deadline lets the job be reclaimed later
        logger.error(s"Failed to record the outcome of job ${job.id}: ${ex.getMessage}", ex)
      }
  }

  // the timeout was already recorded, the late outcome is only logged
  private def logLateOutcome(job: BackgroundJobData, dispatched: Future[Unit]): Unit = {
    dispatched.onComplete {
      case Success(_) => logger.warn(s"Job with id ${job.id} completed after timing out, the result is ignored")
      case Failure(ex) =>
        logger.warn(s"Job with id ${job.id} failed after timing out, the result is ignored: ${ex.getMessage}", ex)
    }
  }
}

object BackgroundJobTypeRunner {
  // Added to the type timeout when claiming, so the in-process timeout always fires and records the failure before
  // the DB deadline lapses; the deadline is only the safety net for jobs whose process crashed.
  val VisibilityMargin: FiniteDuration = 30.seconds
}
