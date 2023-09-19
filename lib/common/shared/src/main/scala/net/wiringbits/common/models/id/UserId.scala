package net.wiringbits.common.models.id

import java.util.UUID

case class UserId private (id: UUID) extends Id {
  override def value: UUID = id
}

object UserId extends Id.Companion[UserId] {
  override def parse(id: UUID): UserId = UserId(id)
}
