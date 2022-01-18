package net.wiringbits.apis

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.AwsConfig

import javax.inject.Inject

class EmailApi @Inject() (
    awsConfig: AwsConfig
) {

  /* To send mails to unverified accounts you need to configure aws to production mode.
     In sandbox mode you can only send emails to verified accounts
      to configure your aws credentials you need to add it at ~/.aws/credentials with
      [default]
        aws_access_key_id = ...
        aws_secret_access_key = ...
   */
  def sendEmail(emailRequest: EmailRequest): Unit = {
    val from = awsConfig.senderEmail

    val htmlBody =
      s"""
      <h1>WIRING BITS</h1>
      <p>${emailRequest.message.body}</p>""".stripMargin

    try {
      val client =
        AmazonSimpleEmailServiceClientBuilder.standard.withRegion(Regions.US_WEST_2).build()
      val destination = new Destination().withToAddresses(emailRequest.destination)
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
  }
}
