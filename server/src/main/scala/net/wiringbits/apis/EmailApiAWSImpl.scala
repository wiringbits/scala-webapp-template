package net.wiringbits.apis

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.{AWSRegionConfig, EmailConfig}
import org.slf4j.LoggerFactory

import javax.inject.Inject
import scala.concurrent.Future

class EmailApiAWSImpl @Inject() (
    emailConfig: EmailConfig,
    regionConfig: AWSRegionConfig
) extends EmailApi {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def sendEmail(emailRequest: EmailRequest): Future[Unit] = {

    val from = emailConfig.senderAddress

    val htmlBody =
      s"""<p>${emailRequest.message.body}</p>""".stripMargin

    try {
      val region = Regions.fromName(regionConfig.region)
      val client =
        AmazonSimpleEmailServiceClientBuilder.standard.withRegion(region).build()
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
      logger.info(s"Sent email to: $from, with subject: ${emailRequest.message.subject}")
      Future.unit
    } catch {
      case ex: Exception =>
        throw new RuntimeException(s"Email was not sent to: $from, with subject: ${emailRequest.message.subject}", ex);
    }
  }
}
