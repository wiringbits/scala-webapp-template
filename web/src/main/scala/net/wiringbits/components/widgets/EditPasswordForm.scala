package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.UpdatePasswordFormData
import net.wiringbits.models.User
import net.wiringbits.ui.components.inputs.PasswordInput
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.Alignment
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import net.wiringbits.AppContext
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html.{form, onSubmit}

import scala.util.{Failure, Success}

@react object EditPasswordForm {
  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        UpdatePasswordFormData.initial(
          oldPasswordLabel = texts.oldPassword,
          passwordLabel = texts.password,
          repeatPasswordLabel = texts.repeatPassword
        )
      )
    )

    def onDataChanged(f: UpdatePasswordFormData => UpdatePasswordFormData): Unit = {
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
          .updatePassword(request)
          .onComplete {
            case Success(_) =>
              // TODO: Show dialog?
              setFormData(_.submitted)

            case Failure(ex) =>
              setFormData(_.submissionFailed(ex.getMessage))
          }
      } else {
        println("Submit fired when it is not available")
      }
    }

    val oldPasswordInput = PasswordInput
      .component(
        PasswordInput.Props(
          formData.data.oldPassword,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(oldPassword = x.oldPassword.updated(value)))
        )
      )

    val passwordInput = PasswordInput
      .component(
        PasswordInput.Props(
          formData.data.password,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(password = x.password.updated(value)))
        )
      )

    val repeatPasswordInput = PasswordInput
      .component(
        PasswordInput.Props(
          formData.data.repeatPassword,
          disabled = formData.isInputDisabled,
          onChange = value => onDataChanged(x => x.copy(repeatPassword = x.repeatPassword.updated(value)))
        )
      )

    val saveButton = {
      val text = if (formData.isSubmitting) {
        Fragment(
          CircularLoader(),
          Container(margin = Container.EdgeInsets.left(8), child = texts.loading)
        )
      } else {
        Fragment(texts.savePassword)
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

    val error =
      formData.firstValidationError.map { text =>
        Container(
          margin = Container.EdgeInsets.vertical(16),
          child = ErrorLabel(text)
        )
      }

    form(onSubmit := (handleSubmit(_)))(
      oldPasswordInput,
      passwordInput,
      repeatPasswordInput,
      Container(
        alignItems = Alignment.center,
        justifyContent = Alignment.center,
        child = error
      ),
      saveButton
    )
  }
}
