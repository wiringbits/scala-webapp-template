package net.wiringbits.common.models.id

import java.util.UUID

case class UserLogId private (id: UUID) extends Id {
  override def value: UUID = id
}

object UserLogId extends Id.Companion[UserLogId] {
  override def parse(id: UUID): UserLogId = UserLogId(id)
}
