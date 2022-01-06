package net.wiringbits.api.forms

case class StatefulFormData[D <: FormData[_]](data: D, state: StatefulFormData.State = StatefulFormData.State.Filling) {
  import StatefulFormData._

  def firstValidationError: Option[String] = {
    state.submissionError.orElse(data.formValidationErrors.headOption)
  }

  // the form is valid when all the data validations succeeded and there are no
  // errors returned by the server in the state
  val isValid: Boolean = data.isValid && state.submissionError.isEmpty

  val isSubmitting: Boolean = state == State.Submitting

  // the input is disabled when the form is being submitted
  val isInputDisabled: Boolean = isSubmitting

  // the submit button is clickable when the data is valid and the form is not being submitted
  val isSubmitButtonEnabled: Boolean = isValid && !isSubmitting
  val isSubmitButtonDisabled: Boolean = !isSubmitButtonEnabled

  // transition the state to submitting
  def submit: StatefulFormData[D] = copy(state = State.Submitting)

  // transition the state to failed with an error message
  def submissionFailed(error: String): StatefulFormData[D] = copy(state = State.Failed(error))

  // transition the state to submitted successfully
  def submitted: StatefulFormData[D] = copy(state = State.Submitted)

  // transition the state to submitted successfully
  def filling: StatefulFormData[D] = copy(state = State.Filling)
}

object StatefulFormData {

  sealed trait State extends Product with Serializable {

    def submissionError: Option[String] = this match {
      case State.Failed(error) => Some(error)
      case _ => None
    }
  }

  object State {
    final case object Filling extends State
    final case object Submitting extends State
    final case object Submitted extends State
    final case class Failed(error: String) extends State
  }
}
