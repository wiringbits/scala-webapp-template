package net.wiringbits.components.pages

import net.wiringbits.components.widgets.Table
import net.wiringbits.ui.components.core.widgets.Title
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import typings.reactRouter.mod.useParams

import scala.scalajs.js

@react object TablePage {
  type Props = Unit

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val params = useParams()
    val table_name = params.asInstanceOf[js.Dynamic].table_name.toString

    Fragment(
      Title(table_name),
      Table()
    )
  }

}
