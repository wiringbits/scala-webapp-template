package net.wiringbits.components.pages

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
import net.wiringbits.common.models.UserToken
import net.wiringbits.components.widgets.{AppCard, ResetPasswordForm}
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.{API, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html.{className, div}
import typings.reactRouter.mod.{useHistory, useParams}

import scala.scalajs.js

@react object ResetPasswordPage {
  case class Props(api: API, loggedIn: User => Unit)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "resetPasswordFormContainer" -> CSSProperties()
          .setMaxWidth(350)
          .setWidth("100%")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val history = useHistory()
    val params = useParams()
    val resetPasswordCode = params.asInstanceOf[js.Dynamic].resetPasswordCode.toString
    val userToken = UserToken.validate(resetPasswordCode)

    Container(
      flex = Some(1),
      justifyContent = Container.Alignment.center,
      alignItems = Container.Alignment.center,
      child = div(className := classes("resetPasswordFormContainer"))(
        AppCard(
          Fragment(
            Container(
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = mui.Typography(AppStrings.enterNewPassword).variant(muiStrings.h5)
            ),
            ResetPasswordForm(props.api, props.loggedIn, userToken),
            Container(
              margin = Container.EdgeInsets.top(8),
              flexDirection = Container.FlexDirection.row,
              alignItems = Container.Alignment.center,
              justifyContent = Container.Alignment.center,
              child = Fragment(
                mui.Typography(AppStrings.alreadyHaveAccount),
                mui
                  .Button(AppStrings.signIn)
                  .variant(muiStrings.text)
                  .color(muiStrings.primary)
                  .onClick(_ => history.push("/signin"))
              )
            )
          )
        )
      )
    )
  }
}
