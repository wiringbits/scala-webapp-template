package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.AppContext
import net.wiringbits.common.ErrorMessages
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.SignInFormData
import net.wiringbits.models.User
import net.wiringbits.ui.components.inputs.{EmailInput, PasswordInput}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks, ReactElement}
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html._
import typings.reactRouterDom.{mod => reactRouterDom}

import scala.util.{Failure, Success}

@react object SignInForm {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = reactRouterDom.useHistory()
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        SignInFormData.initial(
          emailLabel = texts.email,
          passwordLabel = texts.password
        )
      )
    )

    def onDataChanged(f: SignInFormData => SignInFormData): Unit = {
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
          .login(request)
          .onComplete {
            case Success(res) =>
              setFormData(_.submitted)
              props.ctx.loggedIn(User(res.name, res.email))
              history.push("/dashboard") // redirects to the dashboard

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
      child = EmailInput
        .component(
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

    def resendVerifyEmailButton(text: String): ReactElement = {
      // TODO: It would be ideal to match the error against a code than matching a text
      text match {
        case ErrorMessages.`emailNotVerified` =>
          val email = formData.data.email.inputValue

          mui
            .Button(texts.resendEmail)
            .variant(muiStrings.text)
            .color(muiStrings.primary)
            .onClick(_ => history.push(s"/resend-verify-email?email=${email}"))
        case _ => Fragment()
      }
    }

    val error = formData.firstValidationError.map { errorMessage =>
      Container(
        alignItems = Alignment.center,
        margin = Container.EdgeInsets.top(16),
        child = Fragment(
          ErrorLabel(errorMessage),
          resendVerifyEmailButton(errorMessage)
        )
      )
    }

    val recaptcha = ReCaptcha(
      props.ctx,
      onChange = captchaOpt => onDataChanged(x => x.copy(captcha = captchaOpt))
    )

    val loginButton = {
      val text =
        if (formData.isSubmitting)
          Fragment(
            CircularLoader(),
            Container(margin = EdgeInsets.left(8), child = texts.loading)
          )
        else
          Fragment(texts.login)

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant(muiStrings.contained)
        .color(Color.primary)
        .`type`(muiStrings.submit)
    }

    form(
      onSubmit := (handleSubmit(_))
    )(
      Container(
        alignItems = Alignment.center,
        justifyContent = Alignment.center,
        child = Fragment(
          emailInput,
          passwordInput,
          recaptcha,
          error,
          Container(
            minWidth = Some("100%"),
            margin = EdgeInsets.top(16),
            alignItems = Alignment.center,
            child = loginButton
          )
        )
      )
    )
  }
}
