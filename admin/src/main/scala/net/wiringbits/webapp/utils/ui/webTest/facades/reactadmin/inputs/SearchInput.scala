package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.inputs

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import net.wiringbits.webapp.utils.ui.webTest.facades
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.CommonInputProps

import scala.scalajs.js

object SearchInput extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.SearchInput
  override def mkProps = new Props
  class Props extends CommonInputProps {
    val alwaysOn: PropTypes.Prop[Boolean] = of[Boolean]
  }
}
