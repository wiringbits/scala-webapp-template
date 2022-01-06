package net.wiringbits.ui.components.core.widgets

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html._

@react object Scaffold {
  case class Props(appbar: Option[ReactElement] = None, body: ReactElement, footer: Option[ReactElement] = None)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "scaffold" -> CSSProperties()
          .setFlex("auto")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column),
        "scaffoldAppbar" -> CSSProperties(),
        "scaffoldBody" -> CSSProperties()
          .setMinHeight("100vh")
          .setFlex("auto")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setPadding("1em"),
        "scaffoldFooter" -> CSSProperties()
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val appbar = props.appbar match {
      case Some(e) => Fragment(div(className := classes("scaffoldAppbar"))(e))
      case None => Fragment()
    }

    val footer = props.footer match {
      case Some(e) => Fragment(div(className := classes("scaffoldFooter"))(e))
      case None => Fragment()
    }

    div(className := classes("scaffold"))(
      appbar,
      div(className := classes("scaffoldBody"))(props.body),
      footer
    )
  }
}
