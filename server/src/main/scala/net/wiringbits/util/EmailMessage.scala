package net.wiringbits.util

case class EmailMessage(subject: String, body: String)

object EmailMessage {

  def registration(name: String, url: String, emailEndpoint: String): EmailMessage = {
    val subject = "Registration Confirmation"
    val body =
      s"""Hi $name,
         |Thanks for creating an account.
         |To continue, please confirm your email address by clicking the button below.
         |<a href="$url/verify-email/$emailEndpoint">Confirm email address</a>
         |""".stripMargin

    EmailMessage(subject, body)
  }

  def confirm(name: String): EmailMessage = {
    val subject = "Your email has been confirmed"
    val body = s"Hi $name, Thanks for confirming your email.".stripMargin

    EmailMessage(subject, body)
  }
}
