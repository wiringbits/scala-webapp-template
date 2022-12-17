package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import io.github.nafg.simplefacade.PropTypes

trait CommonInputProps extends PropTypes {
  type Fn = () => Unit

  val source: PropTypes.Prop[String] = of[String]
  val className: PropTypes.Prop[String] = of[String]
  val defaultValue: PropTypes.Prop[Any] = of[Any]
  val disabled: PropTypes.Prop[Boolean] = of[Boolean]
  val format: PropTypes.Prop[Fn] = of[Fn]
  val fullWidth: PropTypes.Prop[Boolean] = of[Boolean]
  val helperText: PropTypes.Prop[String] = of[String]
  val label: PropTypes.Prop[String] = of[String]
  val parse: PropTypes.Prop[Fn] = of[Fn]

//  val sx: PropTypes.Prop[Any] = of[Any]
//  val validate: PropTypes.Prop[Fn] = of[Fn]
}
