package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.api.models.AdminGetTableMetadataResponse
import net.wiringbits.ui.components.core.widgets.Container
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object ExperimentalTable {
  case class Props(response: AdminGetTableMetadataResponse)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "table" -> CSSProperties()
          .setWidth("%80"),
        "tableCell" -> CSSProperties()
          .setPadding("5px 10px")
          .setFontSize("0.8rem")
          .setColor("black")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    def formatField(fieldName: String): String = {
      val splittedArray = fieldName.split("_")
      splittedArray.map(_.toLowerCase()).mkString(" ")
    }

    val columns = props.response.columns.map { field =>
      mui
        .TableCell(formatField(field.name))
        .className(classes("tableCell"))
    }

    val rows = props.response.rows.map { record =>
      mui
        .TableRow(
          record.row.map { item =>
            mui
              .TableCell(item.data)
              .className(classes("tableCell"))
          }
        )
    }

    val table =
      mui
        .Table(
          mui.TableHead(
            mui
              .TableRow(
                columns
              )
          ),
          mui.TableBody(
            rows
          )
        )
        .className(classes("table"))

    Container(
      maxWidth = Some("90%"),
      child = table
    )

  }

}
