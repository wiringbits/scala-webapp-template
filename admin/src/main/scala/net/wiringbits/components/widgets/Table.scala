package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.api.models.AdminGetTableMetadataResponse
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object Table {
  case class Props(response: AdminGetTableMetadataResponse)

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

    val tableHead = mui
      .TableRow(
        mui
          .TableCell(
            props.response.name
          ),
        props.response.columns.map(item =>
          mui
            .TableCell(
              mui.Typography(item.name).variant(muiStrings.h6)
            )
            .className(classes("registry"))
        )
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
                .TableCell("Yeahyeahyeah")
                .className(classes("registry"))
            )
            .className(classes("tableRow"))
        )
      )
  }

}
