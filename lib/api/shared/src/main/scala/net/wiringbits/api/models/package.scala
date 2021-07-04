package net.wiringbits.api

import play.api.libs.json._

import java.time.Instant
import java.util.UUID

package object models {

  /**
   * For some reason, play-json doesn't provide support for Instant in the scalajs version,
   * grabbing the jvm values seems to work:
   * - https://github.com/playframework/play-json/blob/master/play-json/jvm/src/main/scala/play/api/libs/json/EnvReads.scala
   * - https://github.com/playframework/play-json/blob/master/play-json/jvm/src/main/scala/play/api/libs/json/EnvWrites.scala
   */
  implicit val instantFormat: Format[Instant] = Format[Instant](
    fjs = implicitly[Reads[String]].map(string => Instant.parse(string)),
    tjs = Writes[Instant](i => JsString(i.toString))
  )

  case class ErrorResponse(error: String)
  implicit val errorResponseFormat: Format[ErrorResponse] = Json.format[ErrorResponse]

  case class CreateUserRequest(name: String, email: String, password: String)
  case class CreateUserResponse(name: String, email: String, token: String)
  implicit val createUserRequestFormat: Format[CreateUserRequest] = Json.format[CreateUserRequest]
  implicit val createUserResponseFormat: Format[CreateUserResponse] = Json.format[CreateUserResponse]

  case class LoginRequest(email: String, password: String)
  case class LoginResponse(name: String, email: String, token: String)
  implicit val loginRequestFormat: Format[LoginRequest] = Json.format[LoginRequest]
  implicit val loginResponseFormat: Format[LoginResponse] = Json.format[LoginResponse]

  case class UpdateUserRequest(name: String)
  case class UpdateUserResponse(noData: String = "")
  implicit val updateUserRequestFormat: Format[UpdateUserRequest] = Json.format[UpdateUserRequest]
  implicit val updateUserResponseFormat: Format[UpdateUserResponse] = Json.format[UpdateUserResponse]

  case class GetCurrentUserRequest(noData: String = "")
  case class GetCurrentUserResponse(id: UUID, name: String, email: String)
  implicit val getUserRequestFormat: Format[GetCurrentUserRequest] = Json.format[GetCurrentUserRequest]
  implicit val getUserResponseFormat: Format[GetCurrentUserResponse] = Json.format[GetCurrentUserResponse]

  case class GetUserLogsRequest(noData: String = "")
  case class GetUserLogsResponse(data: List[GetUserLogsResponse.UserLog])

  object GetUserLogsResponse {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val getUserLogsResponseFormat: Format[UserLog] = Json.format[UserLog]

  }
  implicit val getUserLogsRequestFormat: Format[GetUserLogsRequest] = Json.format[GetUserLogsRequest]
  implicit val getUserLogsResponseFormat: Format[GetUserLogsResponse] = Json.format[GetUserLogsResponse]

  case class AdminGetUserLogsResponse(data: List[AdminGetUserLogsResponse.UserLog])
  implicit val adminGetUserLogsResponseFormat: Format[AdminGetUserLogsResponse] = Json.format[AdminGetUserLogsResponse]

  object AdminGetUserLogsResponse {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val adminGetUserLogsResponseUserLogFormat: Format[UserLog] = Json.format[UserLog]
  }

  case class AdminGetUsersResponse(data: List[AdminGetUsersResponse.User])
  implicit val adminGetUsersResponseFormat: Format[AdminGetUsersResponse] = Json.format[AdminGetUsersResponse]

  object AdminGetUsersResponse {
    case class User(id: UUID, name: String, email: String, createdAt: Instant)
    implicit val adminGetUsersResponseUserFormat: Format[User] = Json.format[User]
  }
}
