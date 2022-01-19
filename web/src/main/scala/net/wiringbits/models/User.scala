package net.wiringbits.models

import net.wiringbits.common.models.{Email, Name}

case class User(name: Name, email: Email, jwt: String)
