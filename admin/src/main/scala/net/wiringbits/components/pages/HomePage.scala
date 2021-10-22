package net.wiringbits.components.pages

import net.wiringbits.ui.components.core.widgets._
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object HomePage {
  type Props = Unit

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Fragment(
      Title("Home Page"),
      Subtitle("This is used to manage details about the whole app")
    )
  }
}
