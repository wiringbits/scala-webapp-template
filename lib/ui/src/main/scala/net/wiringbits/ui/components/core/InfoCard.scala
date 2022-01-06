package net.wiringbits.ui.components.core

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, FlexWrapProperty}
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
import slinky.core.{FunctionalComponent, TagMod}
import slinky.core.annotations.react
import slinky.web.html.{className, div}

@react object InfoCard {
  case class Props(message: String, icon: TagMod[div.tag.type], child: Option[TagMod[div.tag.type]] = None)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "infoCard" -> CSSProperties()
          .setMinHeight(200)
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center")
          .setFontSize("1.5em")
          .setBorderRadius(8)
          .setPadding(16)
          .setOverflow("hidden")
          .setColor("#616161")
          .set(
            "& svg",
            CSSProperties()
              .setFontSize("2em")
              .setMarginBottom(16)
          )
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    mui.Paper
      .className(classes("infoCard"))
      .elevation(0)(
        props.icon,
        props.message,
        props.child.getOrElse("")
      )
  }
}
