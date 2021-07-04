package net.wiringbits.ui.components.core

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object ErrorBoundaryInfo {
  case class Props(error: scala.scalajs.js.Error)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "errorBoundaryInfo" -> CSSProperties()
          .setFlex("auto")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center"),
        "content" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column),
        "icon" -> CSSProperties()
          .setDisplay("flex")
          .setJustifyContent("center")
          .set(
            "& svg ",
            CSSProperties()
              .setFontSize("4em")
          )
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val e = props.error

    div(
      className := classes("errorBoundaryInfo"),
      div(
        className := classes("content"),
        div(className := classes("icon"), muiIcons.Warning()),
        h1("You hit an unexpected error"),
        div(e.toString)
      )
    )
  }

}
