package net.wiringbits.api

package object forms {

  case class FakeFormData(formValidationErrors: List[String] = List.empty, fields: List[FormField[_]] = List.empty)
      extends FormData[String] {

    override def submitRequest: Option[String] = None
  }
}
