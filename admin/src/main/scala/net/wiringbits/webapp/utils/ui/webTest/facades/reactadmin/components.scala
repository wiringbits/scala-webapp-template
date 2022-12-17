package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import io.github.nafg.simplefacade.Implicits._
import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^.VdomNode
import net.wiringbits.webapp.utils.ui.webTest.facades

import scala.scalajs.js

object Admin extends FacadeModule.NodeChildren.Simple {
  val raw: js.Object = facades.reactadmin.ReactAdmin.Admin
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children: PropTypes.Prop[html_<^.VdomNode] = of[VdomNode]
    val dataProvider: PropTypes.Prop[DataProvider] = of[facades.reactadmin.DataProvider]
  }
}

object Create extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.Create
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children: PropTypes.Prop[html_<^.VdomNode] = of[VdomNode]
  }
}

object Datagrid extends FacadeModule.ArrayChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.Datagrid
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children: PropTypes.Prop[html_<^.VdomNode] = of[VdomNode]
    val rowClick: PropTypes.Prop[String] = of[String]
    val bulkActionButtons: PropTypes.Prop[Boolean] = of[Boolean]
  }
}

object Edit extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.Edit
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children: PropTypes.Prop[html_<^.VdomNode] = of[VdomNode]
    val actions: PropTypes.Prop[html_<^.VdomNode] = of[VdomNode]
  }
}

object EditButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.EditButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object SaveButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.SaveButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object DeleteButton extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.DeleteButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object Button extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.Button
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val onClick = of[() => Unit]
  }
  override def mkProps = new Props
}

object Toolbar extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.Toolbar
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
  override def mkProps = new Props
}

object TopToolbar extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.TopToolbar
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
  override def mkProps = new Props
}

object ComponentList extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.List
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val filters = of[List[VdomNode]]
  }
}

object Resource extends FacadeModule.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.Resource
  override def mkProps = new Props
  class Props extends PropTypes {
    val name = of[String]
    val create, edit, list = of[VdomNode]
  }
}

object SimpleForm extends FacadeModule.NodeChildren.Simple {
  override def raw: js.Object = facades.reactadmin.ReactAdmin.SimpleForm
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val toolbar = of[VdomNode]
  }
}
