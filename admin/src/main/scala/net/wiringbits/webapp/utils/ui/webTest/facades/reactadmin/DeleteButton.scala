package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import io.github.nafg.simplefacade._
import net.wiringbits.webapp.utils.ui.webTest.facades

import scala.scalajs.js

object DeleteButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.DeleteButton
  override def mkProps = new Props
  class Props extends PropTypes
}
