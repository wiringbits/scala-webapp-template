package net.wiringbits.apis

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.EmailConfig

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

class EmailApiAWSImpl @Inject() (
    emailConfig: EmailConfig
) extends EmailApi {

  override def sendEmail(emailRequest: EmailRequest): Future[Unit] = {
    val from = emailConfig.senderAddress

    val htmlBody =
      s"""
      <h1>WIRING BITS</h1>
      <p>${emailRequest.message.body}</p>""".stripMargin

    def unsafe(): Unit = try {
      val client =
        AmazonSimpleEmailServiceClientBuilder.standard.build()
      val destination = new Destination().withToAddresses(emailRequest.destination.string)
      val body = new Body()
        .withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
        .withText(new Content().withCharset("UTF-8").withData(emailRequest.message.body))
      val subject = new Content().withCharset("UTF-8").withData(emailRequest.message.subject)
      val message = new Message().withBody(body).withSubject(subject)
      val request = new SendEmailRequest()
        .withDestination(destination)
        .withMessage(message)
        .withSource(from)

      client.sendEmail(request)
      ()
    } catch {
      case ex: Exception =>
        throw new RuntimeException("The email was not sent", ex);
    }

    Future {
      blocking(unsafe())
    }
  }
}
