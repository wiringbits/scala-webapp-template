package net.wiringbits.components.pages

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{
  typographyTypographyMod,
  components => mui,
  materialUiCoreStrings => muiStrings
}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html.{alt, className, img, src}

@react object AboutPage {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "image" -> CSSProperties()
          .setMaxWidth(300)
          .setMaxHeight(164)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val classes = useStyles(())

    val wiringbitsImage =
      img(src := "/img/wiringbits-logo.png", alt := "wiringbits logo", className := classes("image"))
    val repositoryLink = mui
      .Link(texts.checkoutTheRepo)
      .variant(typographyTypographyMod.Style.h5)
      .color(muiStrings.inherit)
      .href("https://github.com/wiringbits/scala-webapp-template")
      .target("_blank")

    Container(
      flex = Some(1),
      alignItems = Alignment.center,
      margin = EdgeInsets.top(48),
      child = Fragment(
        wiringbitsImage,
        Container(
          margin = EdgeInsets.top(32),
          child = repositoryLink
        )
      )
    )
  }
}
