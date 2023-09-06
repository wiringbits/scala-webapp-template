package net.wiringbits.components.widgets

import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object AppCard {
  def apply(child: ReactElement, title: Option[String] = None, centerTitle: Boolean = false): KeyAddingStage =
    component(Props(child = child, title = title, centerTitle = centerTitle))

  case class Props(child: ReactElement, title: Option[String] = None, centerTitle: Boolean = false)

  private val appCardStyling = new CSSPropertiesUtils {
    width = "100%"
    display = "flex"
    flexDirection = FlexDirection.column
    border = "1px solid rgba(0, 0, 0, 0.12)"
    overflow = "hidden"
  }
  private val appCardHeadStyling = new CSSPropertiesUtils {
    padding = "16px 16px 0 16px"
  }

  private val appCardBodyStyling = new CSSPropertiesUtils {
    padding = "25px 16px"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val head: ReactElement = props.title match {
      case Some(title) =>
        val textStyle = new CSSPropertiesUtils {
          textAlign = if (props.centerTitle) "center" else "left"
          fontWeight = 700
        }

        mui.Box.sx(appCardHeadStyling)(
          mui
            .Typography(title)
            .sx(textStyle)
            .variant("h5")
            .color("inherit")
        )
      case None => Fragment()
    }
    val body = mui.Box.sx(appCardBodyStyling)(props.child)

    mui.Paper
      .sx(appCardStyling)
      .elevation(0)(head, body)
  }
}
