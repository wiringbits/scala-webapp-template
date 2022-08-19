package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.react.components.Fragment
import net.wiringbits.AppContext
import net.wiringbits.common.models.Email
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.ResendVerifyEmailFormData
import net.wiringbits.ui.components.inputs.EmailInput
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container, Title}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import org.scalajs.dom
import org.scalajs.dom.URLSearchParams
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._
import typings.reactRouterDom.mod.useLocation
import typings.reactRouterDom.{mod => reactRouterDom}

import scala.util.{Failure, Success}

@react object ResendVerifyEmailForm {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = reactRouterDom.useHistory()
    val params = new URLSearchParams(useLocation().search)
    val emailParam = Option(params.get("email")).getOrElse("")
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        ResendVerifyEmailFormData.initial(
          ResendVerifyEmailFormData.Texts(texts.completeTheCaptcha),
          emailLabel = texts.email,
          emailValue = Some(Email.validate(emailParam))
        )
      )
    )

    def onDataChanged(f: ResendVerifyEmailFormData => ResendVerifyEmailFormData): Unit = {
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
          .sendEmailVerificationToken(request)
          .onComplete {
            case Success(_) =>
              val email = formData.data.email.inputValue

              setFormData(_.submitted)
              history.push(s"/verify-email?email=${email}")

            case Failure(ex) =>
              setFormData(_.submissionFailed(ex.getMessage))
          }
      } else {
        println("Submit fired when it is not available")
      }
    }

    val emailInput = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(8),
      child = EmailInput.component(
        EmailInput.Props(
          formData.data.email,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(email = x.email.updated(value)))
        )
      )
    )

    val error = formData.firstValidationError.map { text =>
      Container(
        margin = Container.EdgeInsets.top(16),
        child = ErrorLabel(text)
      )
    }

    val recaptcha = ReCaptcha(props.ctx, onChange = captchaOpt => onDataChanged(x => x.copy(captcha = captchaOpt)))

    val resendVerifyEmailButton = {
      val text =
        if (formData.isSubmitting) {
          Fragment(
            CircularLoader(),
            Container(margin = EdgeInsets.left(8), child = texts.loading)
          )
        } else Fragment(texts.resendEmail)

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant(muiStrings.contained)
        .color(Color.primary)
        .`type`(muiStrings.submit)
    }

    form(onSubmit := (handleSubmit(_)))(
      mui
        .Paper()
        .elevation(1)(
          Container(
            minWidth = Some("300px"),
            alignItems = Alignment.center,
            padding = EdgeInsets.all(16),
            child = Fragment(
              Title(texts.resendEmail),
              emailInput,
              recaptcha,
              error,
              Container(
                minWidth = Some("100%"),
                margin = EdgeInsets.top(16),
                alignItems = Alignment.center,
                child = resendVerifyEmailButton
              )
            )
          )
        )
    )
  }
}
