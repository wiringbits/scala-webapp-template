package net.wiringbits.apis

import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.{AWSConfig, EmailConfig}
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model._

import javax.inject.Inject
import scala.compat.java8.FutureConverters.CompletionStageOps
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

    def unsafe: Future[Unit] = try {
      val credentials = AwsBasicCredentials.create(awsConfig.accessKeyId.string, awsConfig.secretAccessKey.string)
      val credentialsProvider = StaticCredentialsProvider.create(credentials)

      val client = SesAsyncClient.builder.region(awsConfig.region).credentialsProvider(credentialsProvider).build()

      val destination = Destination.builder.toAddresses(emailRequest.destination.string).build()
      val body = Body.builder
        .html(Content.builder.charset("UTF-8").data(htmlBody).build())
        .text(Content.builder.charset("UTF-8").data(emailRequest.message.body).build())
        .build()
      val subject = Content.builder.charset("UTF-8").data(emailRequest.message.subject).build
      val message = Message.builder.body(body).subject(subject).build()
      val request = SendEmailRequest.builder
        .source(from)
        .destination(destination)
        .message(message)
        .build()
      for {
        response <- blocking {
          client.sendEmail(request)
        }.toScala
        _ = logger.info(
          s"Email sent, to: ${emailRequest.destination}, subject = ${emailRequest.message.subject}, messageId = ${response.messageId()}"
        )
      } yield ()
    } catch {
      case ex: Exception =>
        throw new RuntimeException(
          s"Email was not sent, to: ${emailRequest.destination}, subject = ${emailRequest.message.subject}",
          ex
        )
    }

    Future {
      blocking(unsafe)
    }.flatten
  }
}
