package net.wiringbits.apis

import net.wiringbits.apis.models.EmailRequest

import scala.concurrent.Future

trait EmailApi {
  def sendEmail(emailRequest: EmailRequest): Future[Unit]
}
