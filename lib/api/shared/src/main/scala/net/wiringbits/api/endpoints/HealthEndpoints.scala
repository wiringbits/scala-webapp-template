package net.wiringbits.api.endpoints

import sttp.tapir.*

object HealthEndpoints {
  private val baseEndpoint = endpoint
    .tag("Misc")
    .in("health")

  val check: Endpoint[Unit, Unit, Unit, Unit, Any] = baseEndpoint.get
    .out(emptyOutput.description("The app is healthy"))
    .summary("Queries the application's health")

  val routes: List[AnyEndpoint] = List(
    check
  )
}
