package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.forms.ForgotPasswordFormData
import net.wiringbits.ui.components.inputs.EmailInput
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import net.wiringbits.{API, AppStrings}
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html.{form, onSubmit}
import typings.reactRouter.mod.useHistory

import scala.util.{Failure, Success}

@react object ForgotPasswordForm {
  case class Props(api: API, captchaKey: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val history = useHistory()

    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        ForgotPasswordFormData.initial(
          emailLabel = AppStrings.email
        )
      )
    )

    def onDataChanged(f: ForgotPasswordFormData => ForgotPasswordFormData): Unit = {
      setFormData { current =>
        current.filling.copy(data = f(current.data))
      }
    }

    def handleSubmit(e: SyntheticEvent[_, dom.Event]): Unit = {
      e.preventDefault()

      if (formData.isSubmitButtonEnabled) {
        setFormData(_.submit)
        for {
          request <- formData.data.submitRequest
            .orElse {
              setFormData(_.submissionFailed("Complete the necessary data"))
              None
            }
        } yield props.api.client
          .forgotPassword(request)
          .onComplete {
            case Success(_) =>
              setFormData(_.submitted)
              history.push("/signin") // redirects to sign in page

            case Failure(ex) =>
              setFormData(_.submissionFailed(ex.getMessage))
          }
      } else {
        println("Submit fired when it is not available")
      }
    }

    val forgotPasswordButton = {
      val text = if (formData.isSubmitting) {
        Fragment(
          CircularLoader(),
          Container(margin = Container.EdgeInsets.left(8), child = AppStrings.loading)
        )
      } else {
        Fragment(AppStrings.recover)
      }

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant(muiStrings.contained)
        .color(muiStrings.primary)
        .size(muiStrings.large)
        .`type`(muiStrings.submit)
    }

    val emailInput = EmailInput
      .component(
        EmailInput.Props(
          formData.data.email,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(email = x.email.updated(value)))
        )
      )

    val recaptcha =
      ReCaptcha(
        onChange = captchaOpt => onDataChanged(x => x.copy(captcha = captchaOpt)),
        captchaKey = props.captchaKey
      )

    form(onSubmit := (handleSubmit(_)))(
      Container(
        margin = Container.EdgeInsets.all(16),
        alignItems = Container.Alignment.center,
        child = Fragment(
          emailInput,
          Container(
            margin = Container.EdgeInsets.top(8),
            child = recaptcha
          ),
          formData.firstValidationError.map { text =>
            Container(
              margin = Container.EdgeInsets.top(16),
              child = ErrorLabel(text)
            )
          }
        )
      ),
      Container(
        alignItems = Container.Alignment.center,
        child = forgotPasswordButton
      )
    )
  }
}
