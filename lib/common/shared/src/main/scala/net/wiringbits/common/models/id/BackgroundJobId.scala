package net.wiringbits.common.models.id

import java.util.UUID

case class BackgroundJobId private (id: UUID) extends Id {
  override def value: UUID = id
}

object BackgroundJobId extends Id.Companion[BackgroundJobId] {
  override def parse(id: UUID): BackgroundJobId = BackgroundJobId(id)
}
