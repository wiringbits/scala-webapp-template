package net.wiringbits.components.pages

import net.wiringbits.components.widgets.{AppCard, ResetPasswordForm}
import net.wiringbits.core.I18nHooks
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.Fragment
import typings.reactRouter.mod.{useHistory, useParams}
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.{components => mui}
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.TextAlign

import net.wiringbits.common.models.UserToken
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.AppContext
import slinky.core.FunctionalComponent
import slinky.web.html.{className, div, style}

import scala.scalajs.js

object ResetPasswordPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val styling = new CSSProperties {
    maxWidth = 350
    width = "100%"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory().asInstanceOf[js.Dynamic]
    val params = useParams()
    val resetPasswordCode = params.asInstanceOf[js.Dynamic].resetPasswordCode.toString
    val userToken = UserToken.validate(resetPasswordCode)

    Container(
      flex = Some(1),
      justifyContent = Container.Alignment.center,
      alignItems = Container.Alignment.center,
      child = div(className := "resetPasswordFormContainer", style := styling)(
        AppCard(
          Fragment(
            Container(
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = mui.Typography(texts.enterNewPassword).variant("h5")
            ),
            ResetPasswordForm(props.ctx, userToken),
            Container(
              margin = Container.EdgeInsets.top(8),
              flexDirection = Container.FlexDirection.row,
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = Fragment(
                mui.Typography(texts.alreadyHaveAccount),
                mui.Button
                  .normal(texts.signIn)
                  .variant("text")
                  .color("primary")
                  .onClick(_ => history.push("/signin"))
              )
            )
          )
        )
      )
    )
  }
}
