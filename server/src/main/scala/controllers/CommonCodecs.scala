package controllers

import enumeratum.EnumEntry
import play.api.libs.json._

trait CommonCodecs {

  def wrapperFormat[Outer, Inner: Format](wrap: Inner => Outer, unwrap: Outer => Inner): Format[Outer] =
    new Format[Outer] {
      override def reads(json: JsValue): JsResult[Outer] = {
        json
          .validate[Inner]
          .map(wrap)
      }

      override def writes(o: Outer): JsValue = {
        Json.toJson(unwrap(o))
      }
    }

  def safeWrapperFormat[Outer, Inner: Format](wrap: Inner => Option[Outer], unwrap: Outer => Inner): Format[Outer] =
    new Format[Outer] {
      override def reads(json: JsValue): JsResult[Outer] = {
        json
          .validate[Inner]
          .flatMap { inner =>
            wrap(inner)
              .map(JsSuccess(_))
              .getOrElse(JsError("Invalid value"))
          }
      }

      override def writes(o: Outer): JsValue = {
        Json.toJson(unwrap(o))
      }
    }

  def readsADT[T](pf: PartialFunction[(String, JsValue), JsResult[T]]): Reads[T] = (json: JsValue) => {
    for {
      commandType <- (json \ "type").validate[String]
      jsonData <- (json \ "data").validate[JsValue]
      data <- pf.applyOrElse((commandType, jsonData), (_: (String, JsValue)) => JsError("Invalid value"))
    } yield data
  }

  def writesADT[T](f: T => (String, JsValue)): Writes[T] = (t: T) => {
    val (tpe, data) = f(t)

    Json.obj(
      "type" -> tpe,
      "data" -> data
    )
  }

  def formatADT[T](
      encode: PartialFunction[T, (String, JsValue)],
      decode: PartialFunction[(String, JsValue), JsResult[T]]
  ): Format[T] = {
    Format.apply(readsADT(decode), writesADT(encode))
  }

  def enumFormat[T <: EnumEntry](wrap: String => Option[T]): Format[T] = safeWrapperFormat[T, String](wrap, _.entryName)
}

object CommonCodecs extends CommonCodecs
