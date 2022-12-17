package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.fields

import io.github.nafg.simplefacade.FacadeModule
import net.wiringbits.webapp.utils.ui.webTest.facades
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.CommonInputProps

object TextField extends FacadeModule.Simple {
  override def raw = facades.reactadmin.ReactAdmin.TextField
  class Props extends CommonInputProps
  override def mkProps = new Props
}
