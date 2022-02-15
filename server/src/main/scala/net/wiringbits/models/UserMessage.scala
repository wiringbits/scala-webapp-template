package net.wiringbits.models

sealed trait UserMessage {
  val userId: Int
}

case class UserPointsChangedEvent(userId: Int, points: Int) extends UserMessage
