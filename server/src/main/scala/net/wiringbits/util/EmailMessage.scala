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

  def forgotPassword(name: Name, url: String, emailParameter: String): EmailMessage = {
    val subject = "Password Reset"
    val body =
      s"""<h2>Password Reset Instructions</h2>
         |Hi ${name.string},
         |Here is the link to reset your password.
         |To continue, please click the button below.
         |<a href="$url/reset-password/$emailParameter">Reset your password</a>
         |If you did not perform this request, you can safely ignore this email.
         |""".stripMargin

    EmailMessage(subject, body)
  }

  def resetPassword(name: Name): EmailMessage = {
    val subject = "Your password has been reset"
    val body =
      s"""Hi ${name.string},
         |<h2>Your password has been changed.</h2>
         |If this was not you, click the 'Forgot password' link on the sign in page and follow the steps to reset your password.
         |""".stripMargin

    EmailMessage(subject, body)
  }

  def updatePassword(name: Name): EmailMessage = {
    val subject = "Your password has been updated"
    val body =
      s"""Hi ${name.string},
         |<h2>Your password has been changed.</h2>
         |If this was not you, click the 'Forgot password' link on the sign in page and follow the steps to reset your password.
         |""".stripMargin

    EmailMessage(subject, body)
  }

}
