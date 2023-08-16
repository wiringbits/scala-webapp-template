package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components as mui, materialUiCoreStrings as muiStrings}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import slinky.core.FunctionalComponent
import slinky.core.facade.Fragment

object Loader {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = Fragment(
        CircularLoader(),
        mui.Typography(texts.loading).variant(muiStrings.h6)
      )
    )
  }
}
