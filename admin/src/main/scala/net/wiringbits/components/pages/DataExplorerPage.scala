package net.wiringbits.components.pages

import net.wiringbits.API
import net.wiringbits.components.widgets.ExperimentalTablesWidget
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object DataExplorerPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ExperimentalTablesWidget(props.api)
  }

}
