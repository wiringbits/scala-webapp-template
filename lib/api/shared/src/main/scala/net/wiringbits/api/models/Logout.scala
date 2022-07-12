package net.wiringbits.api.models

import io.swagger.annotations._
import play.api.libs.json.{Format, Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}

object Logout {

  @ApiModel(value = "LogoutRequest", description = "Request to log out of the app")
  case class   Request()

  @ApiModel(value = "LogoutResponse", description = "Response after logging out of the app")
  case class  Response()


 //implicit val logoutRequestFormat: Format[Request] = Json.format[Request]
 //implicit val logoutResponseFormat: Format[Response] = Json.format[Response]

 implicit val logoutRequestFormat = OFormat[Request](Reads[Request] {
         case JsObject(_) => JsSuccess(Request())
         case _           => JsError("Empty object expected")
       }, OWrites[Request] { _ =>
         Json.obj()
       })

implicit val logoutResponseFormat = OFormat[Response](Reads[Response] {
               case JsObject(_) => JsSuccess(Response())
               case _           => JsError("Empty object expected")
             }, OWrites[Response] { _ =>
               Json.obj()
             })
}
