package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import net.wiringbits.webapp.utils.ui.webTest.facades

import scala.scalajs.js

object CreateButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.CreateButton
  override def mkProps = new Props
  class Props extends PropTypes
}
