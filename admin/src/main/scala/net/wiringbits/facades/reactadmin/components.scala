package net.wiringbits.facades.reactadmin

import japgolly.scalajs.react.facade.React.ElementType
import japgolly.scalajs.react.vdom.html_<^.VdomNode
import io.github.nafg.simplefacade.Implicits._
import io.github.nafg.simplefacade.{FacadeModule, PropTypes}

object Admin extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Admin
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val dataProvider = of[DataProvider]
  }
}

object Create extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Create
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
}

object Datagrid extends FacadeModule.ArrayChildren.Simple {
  override def raw = ReactAdmin.Datagrid
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val rowClick = of[String]
  }
}

object Edit extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Edit
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
}

object EditButton extends FacadeModule.Simple {
  override def raw = ReactAdmin.EditButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object List extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.List
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
}

object Resource extends FacadeModule.Simple {
  override def raw = ReactAdmin.Resource
  override def mkProps = new Props
  class Props extends PropTypes {
    val name = of[String]
    val create, edit, list = of[ElementType]
  }
}

object SimpleForm extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.SimpleForm
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
}
