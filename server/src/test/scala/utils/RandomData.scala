package utils

import net.wiringbits.common.models.{Email, Name}

import java.util.UUID

/** Random values for columns that are UNIQUE across the whole database, so tests sharing one Postgres container (and
  * possibly running in parallel) don't collide with each other.
  */
object RandomData {
  def randomString(prefix: String = "t"): String = prefix + UUID.randomUUID().toString.replace("-", "")

  def randomEmail(): Email = Email.trusted(s"test-${UUID.randomUUID()}@wiringbits.net")

  def randomName(): Name = Name.trusted(randomString("name"))
}
