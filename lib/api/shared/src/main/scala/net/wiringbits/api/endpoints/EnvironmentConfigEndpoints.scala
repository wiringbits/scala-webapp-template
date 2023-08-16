package net.wiringbits.api.endpoints

import net.wiringbits.api.models
import net.wiringbits.api.models.{ErrorResponse, GetEnvironmentConfig}
import sttp.tapir.*
import sttp.tapir.json.play.*

object EnvironmentConfigEndpoints {
  private val baseEndpoint = endpoint
    .in("environment-config")
    .tag("Misc")
    .errorOut(errorResponseErrorOut)

  val getEnvironmentConfig: Endpoint[Unit, Unit, ErrorResponse, GetEnvironmentConfig.Response, Any] =
    baseEndpoint.get
      .out(
        jsonBody[GetEnvironmentConfig.Response]
          .description("Got the config values")
          .example(GetEnvironmentConfig.Response("siteKey"))
      )
      .summary("Get the config values for the current environment")
      .description("These values are required by the frontend app to interact with the backend")

  val routes: List[AnyEndpoint] = List(
    getEnvironmentConfig
  )
}
