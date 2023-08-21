package net.wiringbits.components.pages

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.{components=>mui}
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.TextAlign
import slinky.core.{FunctionalComponent, KeyAddingStage}
import net.wiringbits.components.widgets.*
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Title}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.Fragment
import slinky.web.html.{className, div, style}
import typings.reactRouterDom.mod.useHistory
import typings.reactRouterDom.mod as reactRouterDom

import scala.scalajs.js

object SignInPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val styling = new CSSProperties {
    maxWidth=350
    width="100%"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory().asInstanceOf[js.Dynamic]

    Container(
      flex = Some(1),
      justifyContent = Alignment.center,
      alignItems = Alignment.center,
      child = div(className := "signInPageFormContainer",style:=styling)(
        AppCard(
          Fragment(
            Container(
              justifyContent = Alignment.center,
              alignItems = Alignment.center,
              child = Title(texts.signIn)
            ),
            Container(
              flex = Some(1),
              alignItems = Alignment.center,
              justifyContent = Alignment.center,
              padding = EdgeInsets.top(16),
              child = SignInForm(props.ctx)
            ),
            Container(
              margin = Container.EdgeInsets.top(8),
              flexDirection = Container.FlexDirection.row,
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = Fragment(
                mui.Typography(texts.dontHaveAccountYet),
                mui
                  .Button.normal()(texts.signUp)
                  .variant("text")
                  .color("primary")
                  .onClick(_ => history.push("/signUp"))
              )
            ),
            Container(
              flexDirection = Container.FlexDirection.row,
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = Fragment(
                mui.Typography(texts.forgotYourPassword),
                mui
                  .Button.normal(texts.recoverIt)
                  .variant("text")
                  .color("primary")
                  .onClick(_ => history.push("/forgot-password"))
              )
            )
          )
        )
      )
    )
  }
}
