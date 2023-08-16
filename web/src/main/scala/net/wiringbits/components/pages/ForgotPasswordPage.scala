package net.wiringbits.components.pages

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components as mui, materialUiCoreStrings as muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.{AppCard, ForgotPasswordForm}
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.Alignment
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.facade.Fragment
import slinky.core.facade.ReactElement.jsUndefOrToElement
import slinky.web.html.{className, div}
import typings.reactRouterDom.mod.useHistory

import scala.scalajs.js
object ForgotPasswordPage {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "forgotPasswordFormContainer" -> CSSProperties()
          .setMaxWidth(350)
          .setWidth("100%")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val classes = useStyles(())
    val history = useHistory().asInstanceOf[js.Dynamic]

    Container(
      flex = Some(1),
      justifyContent = Alignment.center,
      alignItems = Alignment.center,
      child = div(className := classes("forgotPasswordFormContainer"))(
        AppCard.component(
          AppCard.Props(
            Fragment(
              Container(
                alignItems = Alignment.center,
                child = mui.Typography(texts.recoverYourPassword).variant(muiStrings.h5)
              ),
              ForgotPasswordForm.component(ForgotPasswordForm.Props(props.ctx)),
              Container(
                margin = Container.EdgeInsets.top(8),
                flexDirection = Container.FlexDirection.row,
                alignItems = Container.Alignment.center,
                justifyContent = Container.Alignment.center,
                child = Fragment(
                  mui.Typography(texts.dontHaveAccountYet),
                  mui
                    .Button(texts.signUp)
                    .variant(muiStrings.text)
                    .color(muiStrings.primary)
                    .onClick(_ => history.push("/signUp"))
                )
              )
            )
          )
        )
      )
    )
  }
}
