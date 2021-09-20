package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
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

@react object Table {
  type Props = Unit

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "tableRow" -> CSSProperties().setDisplay("flex"),
        "tableCell" -> CSSProperties()
          .setBorder("1px solid black")
          .setPadding(0),
        "registry" -> CSSProperties()
          .setFlex(1),
        "checkBox" -> CSSProperties().setPadding("1px")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    // This is bad code cause I'm currently working on creating the table
    val tableHead = mui
      .TableRow(
        mui
          .TableCell(
            mui.Checkbox()
          )
          .className(classes("checkBox")),
        mui
          .TableCell("users")
          .className(classes("registry"))
      )
      .className(classes("tableRow"))

    mui
      .Table(
        mui.TableHead(
          tableHead
        ),
        mui.TableBody(
          mui
            .TableRow(
              mui
                .TableCell(
                  mui.Checkbox()
                )
                .className(classes("checkBox")),
              mui
                .TableCell("A")
                .className(classes("registry"))
            )
            .className(classes("tableRow"))
        )
      )
  }

}
