package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
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

import scala.scalajs.js

@react object AppCard {
  case class Props(child: ReactElement, title: Option[String] = None, centerTitle: Boolean = false)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "appCard" -> CSSProperties()
          .setWidth("100%")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setBorder("1px solid rgba(0, 0, 0, 0.12)")
          .setOverflow("hidden"),
        "appCardHead" -> CSSProperties()
          .setPadding("16px 16px 0 16px"),
        "appCardBody" -> CSSProperties()
          .setPadding("25px 16px")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val head = props.title match {
      case Some(title) =>
        val textStyle = js.Dynamic.literal(
          textAlign = if (props.centerTitle) "center" else "left",
          fontWeight = 700
        )
        Fragment(
          div(className := classes("appCardHead"))(
            mui.Typography(style := textStyle, title).variant(muiStrings.h5).color(muiStrings.inherit)
          )
        )
      case None => Fragment()
    }
    val body = div(className := classes("appCardBody"))(props.child)

    mui
      .Paper(className := classes("appCard"))(head, body)
      .elevation(0)
  }
}
