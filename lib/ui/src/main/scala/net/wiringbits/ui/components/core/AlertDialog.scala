package net.wiringbits.ui.components.core

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks

@react object AlertDialog {
  case class Props(visible: Boolean, title: String, message: String, onClose: () => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .Dialog(props.visible)
      .onClose(_ => props.onClose())(
        mui.DialogTitle(props.title),
        mui.DialogContent(mui.DialogContentText(props.message)),
        mui.DialogActions(
          mui.Button().color(PropTypes.Color.secondary).onClick(_ => props.onClose())("Close")
        )
      )
  }
}
