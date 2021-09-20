package net.wiringbits.components.pages

import net.wiringbits.API
import net.wiringbits.components.widgets.Tables
import net.wiringbits.ui.components.core.widgets._
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object HomePage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Fragment(
      Title("Home Page"),
      Tables(props.api),
      Subtitle("This is used to manage details about the whole app")
    )
  }
}
