package net.wiringbits.ui.components.core

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object ConfirmDialog {

  case class Props(
      visible: Boolean,
      title: String,
      message: String,
      onConfirm: () => Unit,
      onCancel: () => Unit
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .Dialog(props.visible)
      .onClose(_ => props.onCancel())(
        mui.DialogTitle(props.title),
        mui.DialogContent(mui.DialogContentText(props.message)),
        mui.DialogActions(
          mui.Button().color(PropTypes.Color.secondary).onClick(_ => props.onCancel())("Cancel"),
          mui
            .Button()
            .color(PropTypes.Color.primary)
            .onClick(_ => props.onConfirm())("Confirm")
        )
      )
  }
}
