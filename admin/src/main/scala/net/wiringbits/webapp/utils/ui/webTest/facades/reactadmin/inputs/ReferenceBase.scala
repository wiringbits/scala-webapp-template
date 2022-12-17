package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.inputs

import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import japgolly.scalajs.react.vdom.VdomNode

trait ReferenceBase extends FacadeModule.NodeChildren.Simple {
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children: PropTypes.Prop[VdomNode] = of[VdomNode]
    val source: PropTypes.Prop[String] = of[String]
    val reference: PropTypes.Prop[String] = of[String]
  }

  override def mkProps = new Props
}
