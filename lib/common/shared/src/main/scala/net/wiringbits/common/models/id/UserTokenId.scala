package net.wiringbits.common.models.id

import java.util.UUID

case class UserTokenId private (id: UUID) extends Id {
  override def value: UUID = id
}

object UserTokenId extends Id.Companion[UserTokenId] {
  override def parse(id: UUID): UserTokenId = UserTokenId(id)
}
