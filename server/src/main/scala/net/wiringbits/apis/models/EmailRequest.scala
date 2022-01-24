package net.wiringbits.apis.models

import net.wiringbits.common.models.Email
import net.wiringbits.util.EmailMessage

case class EmailRequest(destination: Email, message: EmailMessage)
