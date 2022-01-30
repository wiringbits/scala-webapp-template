package net.wiringbits.util

object DelayGenerator {
  def createDelay(
      retry: Int,
      factor: Int = 2
  ): Long = {
    Math
      .pow(
        factor.toDouble,
        retry.toDouble
      )
      .longValue
  }
}
