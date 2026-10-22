package net.wiringbits.core

import org.scalatest.Suite
import play.api.db.Database

trait PostgresSpec {
  self: Suite =>

  def withDatabase[T](runTest: Database => T): T = runTest(SharedPostgres.database)
}
