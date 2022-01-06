package net.wiringbits.ui.components.core.widgets

import com.alexitc.materialui.facade.csstype.mod.BoxSizingProperty
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
import slinky.core.facade.ReactElement
import slinky.web.html._

import scala.scalajs.js

@react object Container {

  case class Props(
      child: ReactElement,
      margin: EdgeInsets = EdgeInsets.all(0),
      padding: EdgeInsets = EdgeInsets.all(0),
      borderRadius: Option[String] = None,
      flex: Option[Int] = None,
      flexDirection: FlexDirection = FlexDirection.column,
      alignItems: Alignment = Alignment.flexStart,
      justifyContent: Alignment = Alignment.flexStart,
      minWidth: Option[String] = None,
      maxWidth: Option[String] = None
  )

  sealed trait FlexDirection extends Product with Serializable

  object FlexDirection {
    final case object column extends FlexDirection
    final case object row extends FlexDirection
  }

  sealed trait Alignment extends Product with Serializable

  object Alignment extends Enumeration {
    final case object center extends Alignment
    final case object flexStart extends Alignment
    final case object flexEnd extends Alignment
    final case object spaceBetween extends Alignment
    final case object spaceAround extends Alignment
    final case object spaceEvenly extends Alignment
  }

  case class EdgeInsets(top: Int, right: Int, bottom: Int, left: Int) {
    def value() = s"${top}px ${right}px ${bottom}px ${left}px"
  }

  object EdgeInsets {
    def all(value: Int): EdgeInsets = EdgeInsets(value, value, value, value)
    def top(value: Int): EdgeInsets = EdgeInsets(value, 0, 0, 0)
    def right(value: Int): EdgeInsets = EdgeInsets(0, value, 0, 0)
    def bottom(value: Int): EdgeInsets = EdgeInsets(0, 0, value, 0)
    def left(value: Int): EdgeInsets = EdgeInsets(0, 0, 0, value)
    def horizontal(value: Int): EdgeInsets = EdgeInsets(0, value, 0, value)
    def vertical(value: Int): EdgeInsets = EdgeInsets(value, 0, value, 0)
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "container" -> CSSProperties()
          .setDisplay("flex")
          .setBoxSizing(BoxSizingProperty.`border-box`)
          .setWidth("auto")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val borderRadius = props.borderRadius.getOrElse("0px")
    val minWidth = props.minWidth.getOrElse("0")
    val maxWidth = props.maxWidth.getOrElse("auto")

    val flex = props.flex.getOrElse("none")

    val containerStyle = js.Dynamic.literal(
      margin = props.margin.value(),
      padding = props.padding.value(),
      borderRadius = borderRadius,
      minWidth = minWidth,
      maxWidth = maxWidth,
      flex = flex.toString,
      flexDirection = props.flexDirection.toString,
      alignItems = parseAlignment(props.alignItems),
      justifyContent = parseAlignment(props.justifyContent)
    )

    div(className := classes("container"), style := containerStyle)(props.child)
  }

  private def parseAlignment(alignment: Alignment): String = {
    alignment match {
      case Alignment.center => "center"
      case Alignment.flexStart => "flex-start"
      case Alignment.flexEnd => "flex-end"
      case Alignment.spaceAround => "space-around"
      case Alignment.spaceBetween => "space-between"
      case Alignment.spaceEvenly => "space-evenly"
    }
  }
}
