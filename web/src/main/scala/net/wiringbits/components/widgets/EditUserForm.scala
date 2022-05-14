package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.AppContext
import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.api.utils.Formatter
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.UpdateInfoFormData
import net.wiringbits.models.User
import net.wiringbits.ui.components.inputs.{EmailInput, NameInput}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html._

import scala.util.{Failure, Success}

@react object EditUserForm {
  case class Props(ctx: AppContext, user: User, response: GetCurrentUser.Response, onSave: () => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val (hasChanges, setHasChanges) = Hooks.useState(false)
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        UpdateInfoFormData.initial(
          nameLabel = texts.name,
          nameInitialValue = Some(props.response.name),
          emailLabel = texts.email,
          emailValue = Some(props.response.email)
        )
      )
    )

    def onDataChanged(f: UpdateInfoFormData => UpdateInfoFormData): Unit = {
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
          .updateUser(request)
          .onComplete {
            case Success(_) =>
              setFormData(_.submitted)
              props.onSave()

            case Failure(ex) =>
              setFormData(_.submissionFailed(ex.getMessage))
          }
      } else {
        println("Submit fired when it is not available")
      }
    }

    val nameInput = NameInput
      .component(
        NameInput.Props(
          formData.data.name,
          disabled = formData.isInputDisabled,
          onChange = value => {
            setHasChanges(value.input != props.response.name.string)
            onDataChanged(x => x.copy(name = x.name.updated(value)))
          }
        )
      )

    val emailInput = EmailInput
      .component(
        EmailInput.Props(
          formData.data.email,
          disabled = true,
          onChange = _ => ()
        )
      )

    val saveButton = {
      val text = if (formData.isSubmitting) {
        Fragment(
          CircularLoader(),
          Container(margin = Container.EdgeInsets.left(8), child = texts.loading)
        )
      } else {
        Fragment(texts.save)
      }

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled || !hasChanges)
        .variant(muiStrings.contained)
        .color(muiStrings.primary)
        .size(muiStrings.large)
        .`type`(muiStrings.submit)
    }

    val createdAt =
      Fragment(
        mui.Typography(texts.createdAt).variant(muiStrings.subtitle2),
        mui.Typography(Formatter.instant(props.response.createdAt))
      )

    form(onSubmit := (handleSubmit(_)))(
      nameInput,
      emailInput,
      formData.firstValidationError.map { text =>
        Container(
          margin = Container.EdgeInsets.top(16),
          child = ErrorLabel(text)
        )
      },
      createdAt,
      saveButton
    )
  }
}
