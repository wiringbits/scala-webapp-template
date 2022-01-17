package net.wiringbits.apis.models

import net.wiringbits.util.EmailMessage

case class EmailRequest(destination: String, message: EmailMessage)
