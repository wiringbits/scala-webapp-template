package net.wiringbits.api.endpoints

import net.wiringbits.api.models
import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers, ErrorResponse}
import net.wiringbits.common.models.{Email, Name}
import sttp.tapir.*
import sttp.tapir.json.play.*

import java.time.Instant
import java.util.UUID

object AdminEndpoints {
  private val baseEndpoint = endpoint
    .in("admin")
    .tag("Admin")
    .in(adminAuth)
    .errorOut(errorResponseErrorOut)

  val getUserLogsEndpoint
      : Endpoint[Unit, (String, UUID, String), ErrorResponse, AdminGetUserLogs.Response, Any] = baseEndpoint.get
    .in("users" / path[UUID]("userId") / "logs")
    .in(adminCookie)
    .out(
      jsonBody[AdminGetUserLogs.Response].example(
        AdminGetUserLogs.Response(
          List(
            AdminGetUserLogs.Response
              .UserLog(
                id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                message = "Message",
                createdAt = Instant.parse("2021-01-01T00:00:00Z")
              )
          )
        )
      )
    )
    .errorOut(oneOf(HttpErrors.badRequest, HttpErrors.unauthorized))
    .summary("Get the logs for a specific user")

  val getUsersEndpoint: Endpoint[Unit, (String, String), ErrorResponse, AdminGetUsers.Response, Any] =
    baseEndpoint.get
      .in("users")
      .in(adminCookie)
      .out(
        jsonBody[AdminGetUsers.Response].example(
          AdminGetUsers.Response(
            List(
              AdminGetUsers.Response.User(
                id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                name = Name.trusted("Alexis"),
                email = Email.trusted("alexis@wiringbits.net"),
                createdAt = Instant.parse("2021-01-01T00:00:00Z")
              )
            )
          )
        )
      )
      .errorOut(oneOf(HttpErrors.badRequest, HttpErrors.unauthorized))
      .summary("Get the registered users")

  val routes: List[AnyEndpoint] = List(
    getUserLogsEndpoint,
    getUsersEndpoint
  )
}
