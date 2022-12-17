package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.fields

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import net.wiringbits.webapp.utils.ui.webTest.facades
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.CommonInputProps

import scala.scalajs.js

object DateField extends FacadeModule.Simple {
  val raw: js.Object = facades.reactadmin.ReactAdmin.DateField
  class Props extends CommonInputProps {
    val showTime: PropTypes.Prop[Boolean] = of[Boolean]
  }
  override def mkProps = new Props
}
