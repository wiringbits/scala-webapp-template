package net.wiringbits.apis

import net.wiringbits.apis.models.EmailRequest
import org.slf4j.LoggerFactory

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait EmailApi {
  def sendEmail(emailRequest: EmailRequest): Future[Unit]
}

object EmailApi {
  class LogImpl @Inject() (implicit ec: ExecutionContext) extends EmailApi {
    private val logger = LoggerFactory.getLogger(this.getClass)

    override def sendEmail(request: EmailRequest): Future[Unit] = Future {
      logger.info(
        s"Sending email, to = ${request.destination}, subject = ${request.message.subject}, body = ${request.message.body}"
      )
    }
  }
}
