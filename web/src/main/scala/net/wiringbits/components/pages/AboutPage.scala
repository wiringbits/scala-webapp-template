package net.wiringbits.components.pages

import com.olvind.mui.propTypes
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.react.mod.CSSProperties
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.Fragment
import slinky.web.html.{alt, className, img, src, style}

object AboutPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val styling=new CSSProperties {
    maxWidth=300
    maxHeight=164
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    val wiringbitsImage =
      img(src := "/img/wiringbits-logo.png", alt := "wiringbits logo", className := "image",style:=styling)
    val repositoryLink = mui
      .Link(texts.checkoutTheRepo)
      .variant("h5")
      .color("inherit")
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
