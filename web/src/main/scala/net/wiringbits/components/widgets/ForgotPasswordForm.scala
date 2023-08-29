package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.ForgotPasswordFormData
import net.wiringbits.ui.components.inputs.EmailInput
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.useHistory
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, KeyAddingStage, SyntheticEvent}
import slinky.web.html.{form, onSubmit}

import scala.scalajs.js
import scala.util.{Failure, Success}

object ForgotPasswordForm {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()

    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        ForgotPasswordFormData.initial(
          emailLabel = texts.email
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
              setFormData(_.submissionFailed(texts.completeData))
              None
            }
        } yield props.ctx.api.client
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
          Container(margin = Container.EdgeInsets.left(8), child = texts.loading)
        )
      } else {
        Fragment(texts.recover)
      }

      mui.Button
        .normal()(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant("contained")
        .color("primary")
        .size("large")
        .`type`("submit")
    }

    val emailInput = EmailInput
      .component(
        EmailInput.Props(
          formData.data.email,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(email = x.email.updated(value)))
        )
      )

    val recaptcha = ReCaptcha(props.ctx, onChange = captchaOpt => onDataChanged(x => x.copy(captcha = captchaOpt)))

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
