package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import io.github.nafg.simplefacade.Implicits._
import io.github.nafg.simplefacade._
import japgolly.scalajs.react.vdom.VdomNode
import net.wiringbits.webapp.utils.ui.webTest.facades

import scala.scalajs.js

object FilterButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.FilterButton
  override def mkProps = new Props
  class Props extends PropTypes {
    val filters: PropTypes.Prop[List[VdomNode]] = of[List[VdomNode]]
  }
}
