package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.{components => mui}
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiIconsMaterial.{components => muiIcons}

import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html.*

import scala.scalajs.js

object AppCard {
  def apply(child: ReactElement, title: Option[String] = None, centerTitle: Boolean = false): KeyAddingStage =
    component(Props(child = child, title = title, centerTitle = centerTitle))

  case class Props(child: ReactElement, title: Option[String] = None, centerTitle: Boolean = false)

  val appCardStyling = new CSSProperties {
    width = "100%"
    display = "flex"
    flexDirection = FlexDirection.column
    border = "1px solid rgba(0, 0, 0, 0.12)"
    overflow = "hidden"
  }
  val appCardHeadStyling = new CSSProperties {
    padding = "16px 16px 0 16px"
  }
  val appCardBodyStyling = new CSSProperties {
    padding = "25px 16px"
  }
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>

    val head = props.title match {
      case Some(title) =>
        val textStyle = js.Dynamic.literal(
          textAlign = if (props.centerTitle) "center" else "left",
          fontWeight = 700
        )
        Fragment(
          div(className := "appCardHead", style := appCardHeadStyling)(
            mui
              .Typography(style := textStyle, title)
              .variant("h5")
              .color("inherit")
          )
        )
      case None => Fragment()
    }
    val body = div(className := "appCardBody", style := appCardBodyStyling)(props.child)

    mui
      .Paper(className := "appCard", style := appCardStyling)(head, body)
      .elevation(0)
  }
}
