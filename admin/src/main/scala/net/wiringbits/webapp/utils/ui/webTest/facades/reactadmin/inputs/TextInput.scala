package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.inputs

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.{CommonInputProps, ReactAdmin}

import scala.scalajs.js

object TextInput extends FacadeModule.Simple {
  override def raw: js.Object = ReactAdmin.TextInput
  class Props extends CommonInputProps {
    val alwaysOn: PropTypes.Prop[Boolean] = of[Boolean]
  }
  override def mkProps = new Props
}
