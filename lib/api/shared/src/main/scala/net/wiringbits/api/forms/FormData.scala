package net.wiringbits.api.forms

trait FormData[RequestModel] {
  private def mandatoryFieldsCompleted: Boolean = fields.filter(_.required).forall(_.isValid)
  private def optionalFieldsValid: Boolean = fields.filterNot(_.required).forall(_.isValid)

  // the list of typed fields available for the form
  def fields: List[FormField[_]]

  def fieldsError: Option[String] = {
    if (!mandatoryFieldsCompleted) {
      Some("The mandatory fields need to be filled")
    } else if (!optionalFieldsValid) {
      Some("The optional fields need to be either absent or be valid")
    } else {
      None
    }
  }

  // custom validation errors, usually, complex logic that involves many fields from the form
  def formValidationErrors: List[String]

  // true when the form is in a valid state
  def isValid: Boolean = {
    mandatoryFieldsCompleted &&
    optionalFieldsValid &&
    formValidationErrors.isEmpty
  }

  def submitRequest: Option[RequestModel]
}
