package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.SignUpFormData
import net.wiringbits.ui.components.inputs.{EmailInput, NameInput, PasswordInput}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container, Title}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import net.wiringbits.AppContext
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html._
import typings.reactRouterDom.{mod => reactRouterDom}

import scala.util.{Failure, Success}

@react object SignUpForm {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = reactRouterDom.useHistory()
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        SignUpFormData.initial(
          nameLabel = texts.name,
          emailLabel = texts.email,
          passwordLabel = texts.password,
          repeatPasswordLabel = texts.repeatPassword
        )
      )
    )

    def onDataChanged(f: SignUpFormData => SignUpFormData): Unit = {
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
          .createUser(request)
          .onComplete {
            case Success(_) =>
              setFormData(_.submitted)
              history.push("/verify-email") // redirects to email page

            case Failure(ex) =>
              setFormData(_.submissionFailed(ex.getMessage))
          }
      } else {
        println("Submit fired when it is not available")
      }
    }

    val nameInput = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(8),
      child = NameInput.component(
        NameInput.Props(
          formData.data.name,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(name = x.name.updated(value)))
        )
      )
    )

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

    val passwordInput = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(16),
      child = PasswordInput
        .component(
          PasswordInput.Props(
            formData.data.password,
            disabled = formData.isInputDisabled,
            onChange = value => onDataChanged(x => x.copy(password = x.password.updated(value)))
          )
        )
    )

    val repeatPasswordInput = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(16),
      child = PasswordInput
        .component(
          PasswordInput.Props(
            formData.data.repeatPassword,
            disabled = formData.isInputDisabled,
            onChange = value => onDataChanged(x => x.copy(repeatPassword = x.repeatPassword.updated(value)))
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

    val signUpButton = {
      val text =
        if (formData.isSubmitting) {
          Fragment(
            CircularLoader(),
            Container(margin = EdgeInsets.left(8), child = texts.loading)
          )
        } else Fragment(texts.createAccount)

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant(muiStrings.contained)
        .color(Color.primary)
        .`type`(muiStrings.submit)
    }

    // TODO: Use a form to get the enter key submitting the form
    form(onSubmit := (handleSubmit(_)))(
      mui
        .Paper()
        .elevation(1)(
          Container(
            minWidth = Some("300px"),
            alignItems = Alignment.center,
            padding = EdgeInsets.all(16),
            child = Fragment(
              Title(texts.signUp),
              nameInput,
              emailInput,
              passwordInput,
              repeatPasswordInput,
              recaptcha,
              error,
              Container(
                minWidth = Some("100%"),
                margin = EdgeInsets.top(16),
                alignItems = Alignment.center,
                child = signUpButton
              )
            )
          )
        )
    )
  }
}
