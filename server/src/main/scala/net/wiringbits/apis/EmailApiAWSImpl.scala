package net.wiringbits.apis

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.{AWSConfig, EmailConfig}
import org.slf4j.LoggerFactory

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

class EmailApiAWSImpl @Inject() (
    emailConfig: EmailConfig,
    awsConfig: AWSConfig
) extends EmailApi {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def sendEmail(emailRequest: EmailRequest): Future[Unit] = {

    val from = emailConfig.senderAddress

    val htmlBody =
      s"""<p>${emailRequest.message.body}</p>""".stripMargin

    def unsafe(): Unit = try {
      val region = Regions.fromName(awsConfig.region)
      val credentials = new BasicAWSCredentials(awsConfig.accessKeyId.string, awsConfig.secretAccessKey.string)
      val credentialsProvider = new AWSStaticCredentialsProvider(credentials)

      val client =
        AmazonSimpleEmailServiceClientBuilder.standard.withCredentials(credentialsProvider).withRegion(region).build()
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
      logger.info(s"Email sent, to: ${emailRequest.destination}, subject = ${emailRequest.message.subject}")
    } catch {
      case ex: Exception =>
        throw new RuntimeException(
          s"Email was not sent, to: ${emailRequest.destination}, subject = ${emailRequest.message.subject}",
          ex
        );
    }

    Future {
      blocking(unsafe())
    }
  }
}
