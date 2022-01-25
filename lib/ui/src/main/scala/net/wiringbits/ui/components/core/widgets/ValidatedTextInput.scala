package net.wiringbits.ui.components.core.widgets

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.webapp.common.validators.{TextValidator, ValidationResult}
import net.wiringbits.webapp.utils.slinkyUtils.forms.FormField
import org.scalajs.dom
import slinky.core.FunctionalComponent

abstract class ValidatedTextInput[T: TextValidator] {
  private val validator = implicitly[TextValidator[T]]

  case class Props(
      field: FormField[T],
      disabled: Boolean = false,
      onChange: ValidationResult[T] => Unit,
      margin: PropTypes.Margin = muiStrings.dense
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    def onChange(text: String): Unit = {
      val validation = validator(text)
      props.onChange(validation)
    }

    val helperText = props.field.value.flatMap(_.errorMessage).getOrElse("")
    val value = props.field.value.map(_.input).getOrElse("")
    val hasError = props.field.value.exists(_.hasError)
    mui.TextField
      .OutlinedTextFieldProps()
      .id(s"ExperimentalTextInput-${props.field.name}")
      .name(s"ExperimentalTextInput-${props.field.name}")
      .label(props.field.label)
      .`type`(props.field.`type`)
      .required(props.field.required)
      .fullWidth(true)
      .disabled(props.disabled)
      .margin(props.margin)
      .error(hasError)
      .helperText(helperText)
      .value(value)
      .onChange(e => onChange(e.target.asInstanceOf[dom.HTMLInputElement].value))
  }
}
