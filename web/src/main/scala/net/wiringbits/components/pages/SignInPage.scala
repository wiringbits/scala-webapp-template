package net.wiringbits.components.pages

import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.*
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Title}
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.useHistory
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object SignInPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  private val styling = new CSSPropertiesUtils {
    maxWidth = 350
    width = "100%"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()

    Container(
      flex = Some(1),
      justifyContent = Container.Alignment.center,
      alignItems = Container.Alignment.center,
      child = mui.Box.sx(styling)(
        AppCard(
          Fragment(
            Container(
              justifyContent = Container.Alignment.center,
              alignItems = Container.Alignment.center,
              child = Title(texts.signIn)
            ),
            Container(
              flex = Some(1),
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              padding = Container.EdgeInsets.top(16),
              child = SignInForm(props.ctx)
            ),
            Container(
              margin = Container.EdgeInsets.top(8),
              flexDirection = FlexDirection.row,
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = Fragment(
                mui.Typography(texts.dontHaveAccountYet),
                mui.Button
                  .normal()(texts.signUp)
                  .variant("text")
                  .color("primary")
                  .onClick(_ => history.push("/signUp"))
              )
            ),
            Container(
              flexDirection = FlexDirection.row,
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = Fragment(
                mui.Typography(texts.forgotYourPassword),
                mui.Button
                  .normal(texts.recoverIt)
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
