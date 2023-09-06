package net.wiringbits.components.pages

import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.{AppCard, ForgotPasswordForm}
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.useHistory
import slinky.core.facade.Fragment
import slinky.core.facade.ReactElement.jsUndefOrToElement
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js
object ForgotPasswordPage {
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
              alignItems = Container.Alignment.center,
              child = mui.Typography(texts.recoverYourPassword).variant("h5")
            ),
            ForgotPasswordForm(props.ctx),
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
            )
          )
        )
      )
    )
  }
}
