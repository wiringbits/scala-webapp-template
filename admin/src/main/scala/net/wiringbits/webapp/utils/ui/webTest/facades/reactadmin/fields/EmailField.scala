package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.fields

import io.github.nafg.simplefacade.FacadeModule
import net.wiringbits.webapp.utils.ui.webTest.facades
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.CommonInputProps

import scala.scalajs.js

object EmailField extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.EmailField
  class Props extends CommonInputProps
  override def mkProps = new Props
}
