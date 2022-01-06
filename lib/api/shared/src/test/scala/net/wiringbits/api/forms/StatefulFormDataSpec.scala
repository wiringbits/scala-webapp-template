package net.wiringbits.api.forms

import org.scalatest.OptionValues._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class StatefulFormDataSpec extends AnyWordSpec {
  "firstValidationError" should {
    "return the submission error when the submission has failed" in {
      val data = FakeFormData()
      val error = "Invalid data"
      val form = StatefulFormData(data, StatefulFormData.State.Failed(error))
      form.firstValidationError.value must be(error)
    }

    "return the first form validation error" in {
      val error = "Invalid data"
      val data = FakeFormData(formValidationErrors = List(error, "Another error"))
      val form = StatefulFormData(data)
      form.firstValidationError.value must be(error)
    }

    "return None when there are no errors" in {
      val data = FakeFormData()
      val form = StatefulFormData(data)
      form.firstValidationError must be(empty)
    }
  }

  "isValid" should {
    "return true when there are no data validation errors nor submission errors" in {
      val data = FakeFormData()
      val form = StatefulFormData(data)
      form.isValid must be(true)
    }

    "return false when there are submission errors" in {
      val data = FakeFormData()
      val form = StatefulFormData(data, StatefulFormData.State.Failed("error"))
      form.isValid must be(false)
    }

    "return false when there are data validation errors" in {
      val data = FakeFormData(formValidationErrors = List("error"))
      val form = StatefulFormData(data)
      form.isValid must be(false)
    }
  }

  "isSubmitting & isInputDisabled" should {
    val data = FakeFormData()

    "return true when the state is Submitting" in {
      val form = StatefulFormData(data, StatefulFormData.State.Submitting)
      form.isSubmitting must be(true)
      form.isInputDisabled must be(true)
    }

    "return false when the state is not Submitting" in {
      List(StatefulFormData.State.Filling, StatefulFormData.State.Submitted, StatefulFormData.State.Failed("error"))
        .map(state => StatefulFormData(data, state))
        .foreach { form =>
          form.isSubmitting must be(false)
          form.isInputDisabled must be(false)
        }
    }
  }

  "isSubmitButtonEnabled" should {
    "return true when the data is valid and the state is not submitting" in {
      val data = FakeFormData()
      List(StatefulFormData.State.Filling, StatefulFormData.State.Submitted)
        .map(state => StatefulFormData(data, state))
        .foreach { form =>
          form.isSubmitButtonEnabled must be(true)
        }
    }

    "return false when the data is not valid" in {
      val data = FakeFormData(formValidationErrors = List("error"))
      val form = StatefulFormData(data)
      form.isSubmitButtonEnabled must be(false)
    }

    "return false when state is submitting" in {
      val data = FakeFormData()
      val form = StatefulFormData(data, StatefulFormData.State.Submitting)
      form.isSubmitButtonEnabled must be(false)
    }
  }

  "isSubmitButtonDisabled" should {
    "return false when the data is valid and the state is not submitting" in {
      val data = FakeFormData()
      List(StatefulFormData.State.Filling, StatefulFormData.State.Submitted)
        .map(state => StatefulFormData(data, state))
        .foreach { form =>
          form.isSubmitButtonDisabled must be(false)
        }
    }

    "return true when the data is not valid" in {
      val data = FakeFormData(formValidationErrors = List("error"))
      val form = StatefulFormData(data)
      form.isSubmitButtonDisabled must be(true)
    }

    "return true when state is submitting" in {
      val data = FakeFormData()
      val form = StatefulFormData(data, StatefulFormData.State.Submitting)
      form.isSubmitButtonDisabled must be(true)
    }
  }

  "submit" should {
    "transition the state to Submitting" in {
      val data = FakeFormData()
      val form = StatefulFormData(data)
      form.submit.state must be(StatefulFormData.State.Submitting)
    }
  }

  "submissionFailed" should {
    "transition the state to SubmissionFailed" in {
      val data = FakeFormData()
      val form = StatefulFormData(data)
      val error = "error"
      form.submissionFailed(error).state must be(StatefulFormData.State.Failed(error))
    }
  }

  "submitted" should {
    "transition the state to Submitted" in {
      val data = FakeFormData()
      val form = StatefulFormData(data)
      form.submitted.state must be(StatefulFormData.State.Submitted)
    }
  }

  "filling" should {
    "transition the state to Filling" in {
      val data = FakeFormData()
      List(
        StatefulFormData.State.Filling,
        StatefulFormData.State.Submitted,
        StatefulFormData.State.Submitting,
        StatefulFormData.State.Failed("error")
      )
        .map(state => StatefulFormData(data, state))
        .foreach { form =>
          form.filling.state must be(StatefulFormData.State.Filling)
        }
    }
  }
}
