package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.inputs

import io.github.nafg.simplefacade.FacadeModule
import net.wiringbits.webapp.utils.ui.webTest.facades
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.CommonInputProps

import scala.scalajs.js

object DateInput extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.DateInput
  override def mkProps = new Props
  class Props extends CommonInputProps
}
