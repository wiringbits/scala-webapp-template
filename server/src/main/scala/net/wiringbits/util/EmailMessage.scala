package net.wiringbits.util

import net.wiringbits.common.models.Name

case class EmailMessage(subject: String, body: String)

object EmailMessage {

  def registration(name: Name, url: String, emailParameter: String): EmailMessage = {
    val subject = "Registration Confirmation"
    val body =
      s"""Hi ${name.string},
         |Thanks for creating an account.
         |To continue, please confirm your email address by clicking the button below.
         |<a href="$url/verify-email/$emailParameter">Confirm email address</a>
         |""".stripMargin

    EmailMessage(subject, body)
  }

  def confirm(name: Name): EmailMessage = {
    val subject = "Your email has been confirmed"
    val body = s"Hi ${name.string}, Thanks for confirming your email.".stripMargin

    EmailMessage(subject, body)
  }
}
