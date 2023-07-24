package net.wiringbits.api.endpoints

import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.*

object HealthEndpoints {
  private val baseEndpoint = endpoint
    .tag("Misc")
    .in("health")

  val check: Endpoint[Unit, Unit, Unit, CookieValueWithMeta, Any] = baseEndpoint.get
    .out(emptyOutput.description("The app is healthy"))
    .out(setCookie("Hello"))
    .summary("Queries the application's health")

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    check
  )
}
