package net.wiringbits.actions.internal

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import net.wiringbits.repositories.BackgroundJobsRepository
import net.wiringbits.repositories.models.BackgroundJobData
import org.slf4j.LoggerFactory

import javax.inject.Inject
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

class StreamPendingBackgroundJobsForeverAction @Inject() (backgroundJobsRepository: BackgroundJobsRepository)(implicit
    ec: ExecutionContext,
    system: ActorSystem
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def apply(reconnectionDelay: FiniteDuration = 10.seconds): Source[BackgroundJobData, akka.NotUsed] = {
    // Let's use unfoldAsync to continuously fetch items from database
    // First execution doesn't involve a delay
    Source
      .unfoldAsync[Boolean, Source[BackgroundJobData, Future[Int]]](false) { delay =>
        logger.trace(s"Looking for pending background jobs")
        akka.pattern
          .after(if (delay) reconnectionDelay else 0.seconds) {
            backgroundJobsRepository.streamPendingJobs
          }
          .map(source => Some(true -> source))
      }
      .flatMapConcat(identity)
  }
}
