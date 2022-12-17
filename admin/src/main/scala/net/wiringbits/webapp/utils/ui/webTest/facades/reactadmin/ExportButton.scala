package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import net.wiringbits.webapp.utils.ui.webTest.facades

import scala.scalajs.js

object ExportButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.ExportButton
  override def mkProps = new Props
  class Props extends PropTypes
}
