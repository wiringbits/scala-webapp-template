package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object OutlinedTextInput {
  case class Props(name: String, value: String, onChange: String => Unit, disabled: Boolean = false)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.TextField
      .OutlinedTextFieldProps()
      .name(props.name)
      .label(props.name)
      .placeholder(props.name)
      .margin(muiStrings.dense)
      .fullWidth(true)
      .value(props.value)
      .onChange(evt => props.onChange(evt.target.asInstanceOf[dom.HTMLInputElement].value))
      .disabled(props.disabled)
  }
}
