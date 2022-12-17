package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.inputs

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import net.wiringbits.webapp.utils.ui.webTest.facades

object SelectInput extends FacadeModule.Simple {
  override def raw = facades.reactadmin.ReactAdmin.SelectInput
  class Props extends PropTypes {
    val optionText = of[String]
    val disabled = of[Boolean]
  }
  override def mkProps = new Props
}
