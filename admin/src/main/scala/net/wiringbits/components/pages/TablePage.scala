package net.wiringbits.components.pages

import net.wiringbits.ui.components.core.widgets.{Subtitle, Title}
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}

@react object TablePage {
  type Props = Unit

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val pathname = dom.window.location.pathname
    val (routeValue, _) = Hooks.useState(pathname)

    Fragment(
      Title(routeValue),
      Subtitle("User list")
    )
  }

}
