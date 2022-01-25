package net.wiringbits.components.widgets

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
import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.api.utils.Formatter
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object UserEditSummaryView {
  case class Props(user: GetCurrentUser.Response, onChange: GetCurrentUser.Response => Unit)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "userEditSummaryView" -> CSSProperties()
          .setDisplay("flex")
          .set("& > *", CSSProperties().setMarginRight(16))
          .set("& > *:last-child", CSSProperties().setMarginRight(0)),
        "section" -> CSSProperties()
          .setFlex(1)
          .setDisplay("flex")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    div(className := classes("userEditSummaryView"))(
      div(className := classes("section"))(UserEditForm(props.user, props.onChange)),
      div(className := classes("section"))(
        div()(
          mui.Typography("Created at").variant(muiStrings.subtitle2),
          mui.Typography(Formatter.instant(props.user.createdAt))
        )
      )
    )
  }
}
