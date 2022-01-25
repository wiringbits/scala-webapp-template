package net.wiringbits.components.widgets

import net.wiringbits.AppStrings
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks

@react object UserEditPasswordView {
  case class Props(password: String, onChange: String => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (password, setPassword) = Hooks.useState[String](props.password)

    def onChange(value: String): Unit = {
      setPassword(value)
      props.onChange(value)
    }

    OutlinedTextInput(
      AppStrings.password,
      password,
      value => onChange(value)
    )
  }
}
