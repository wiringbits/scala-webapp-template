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

@react object Table {
  case class Props(response: AdminGetTableMetadataResponse)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "table" -> CSSProperties()
          .setWidth("%80"),
        "tableCell" -> CSSProperties()
          .setPadding("5px")
          .setFontSize("0.8rem")
          .setColor("black")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val columns = props.response.columns.map { item =>
      mui
        .TableCell(
          item.name
        )
        .className(classes("tableCell"))
    }

    def slungifyReverse(fieldName: String): String = {
      // Creo que es mejor usa regEx para esto y evitas complicarte la vida
      val splittedArray = fieldName.split("_")
      splittedArray.mapInPlace(word => word.toLowerCase())
      splittedArray.mkString(" ")
    }

    val rows = props.response.rows.map { row =>
      mui
        .TableRow(
          row.row
            .map { item =>
              mui
                .TableCell(slungifyReverse(item.data))
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
      maxWidth = Some("80%"),
      child = table
    )

  }

}
