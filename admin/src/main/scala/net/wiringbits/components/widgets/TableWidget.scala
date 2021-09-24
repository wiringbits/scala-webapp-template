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
import typings.reactRouter.mod.useParams

import scala.scalajs.js

@react object TableWidget {
  case class Props(api: API)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme => StringDictionary()
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val params = useParams()
    val tableName = params.asInstanceOf[js.Dynamic].tableName.toString

    RemoteDataLoader.component[AdminGetTableMetadataResponse](
      RemoteDataLoader
        .Props(
          fetch = () => props.api.client.adminGetTableMetadata(tableName),
          render = response => Table.component(Table.Props(response)),
          progressIndicator = () => Loader()
        )
    )
  }

}
