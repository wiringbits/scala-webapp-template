package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{StyleRulesCallback, Styles, WithStylesOptions}
import net.wiringbits.API
import net.wiringbits.api.models.AdminGetTableMetadataResponse
import net.wiringbits.ui.components.core.RemoteDataLoader
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import typings.reactRouter.mod.{useLocation, useParams}
import typings.std.global.URLSearchParams

import scala.scalajs.js
import scala.util.Try

@react object ExperimentalTableWidget {
  case class Props(api: API)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme => StringDictionary()
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val params = useParams()
    val tableName = params.asInstanceOf[js.Dynamic].tableName.toString

    val offSetOption = Try(new URLSearchParams(useLocation().search).get("offset").toString.toInt).toOption
    val limitOption = Try(new URLSearchParams(useLocation().search).get("limit").toString.toInt).toOption
    val pageLength = 10

    RemoteDataLoader.component[AdminGetTableMetadataResponse](
      RemoteDataLoader
        .Props(
          fetch = () =>
            props.api.client
              .adminGetTableMetadata(tableName, offSetOption.getOrElse(0), limitOption.getOrElse(pageLength)),
          render = response => ExperimentalTable.component(ExperimentalTable.Props(response)),
          progressIndicator = () => Loader()
        )
    )
  }

}
