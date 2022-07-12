package net.wiringbits.api.models
import play.api.libs.json.{Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}
object RequestResponseCodec {

private [models] def requestResponseCodec[A](value:A): OFormat[A]  = OFormat[A](Reads[A] {
          case JsObject(_) => JsSuccess[String]("").map(_ => value)
          case _           => JsError("Empty object expected")
        }, OWrites[A] { _ =>
          Json.obj()
        })

}
