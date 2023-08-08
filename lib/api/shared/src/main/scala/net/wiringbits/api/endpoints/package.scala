package net.wiringbits.api

import net.wiringbits.api.models.{ErrorResponse, errorResponseFormat}
import sttp.model.StatusCode
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.EndpointInput.AuthType
import sttp.tapir.json.play.*
import sttp.tapir.{EndpointIO, *}
import sttp.tapir.generic.auto.*

import java.util.UUID

package object endpoints {
  // TODO: better name?
  object HttpErrors {
    val badRequest: EndpointOutput.OneOfVariant[Unit] = oneOfVariant(
      statusCode(StatusCode.BadRequest).description("Invalid or missing arguments")
    )

    val unauthorized: EndpointOutput.OneOfVariant[Unit] = oneOfVariant(
      statusCode(StatusCode.Unauthorized).description("Invalid or missing authentication")
    )
  }

  val adminCookie: EndpointInput.Cookie[String] = cookie[String]("X-Forwarded-User")
    .default("Unknown")
    .schema(_.hidden(true))

  val adminAuth: EndpointInput.Auth[String, AuthType.Http] = auth
    .basic[String]()
    .securitySchemeName("Basic authorization")
    .description("Admin credentials")

  val userIdCookie: EndpointIO.Header[Option[String]] = header[Option[String]]("Cookie")
    .description("User session")
    .schema(_.hidden(true))

  val setUserIdCookie: EndpointIO.Header[String] = header[String]("Set-Cookie")
    .description("User session")
    .schema(_.hidden(true))
//    setCookie("userId")
//    .description("User session")
//    .schema(_.hidden(true))

  val errorResponseErrorOut: EndpointIO.Body[String, ErrorResponse] = jsonBody[ErrorResponse]
    .description("Error response")
    .example(ErrorResponse("Unauthorized: Invalid or missing authentication"))
    .schema(_.hidden(true))
}
