package net.wiringbits.components.pages

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
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
import net.wiringbits.components.widgets._
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Title}
import net.wiringbits.{API, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html.{className, div}
import typings.reactRouterDom.mod.useHistory

@react object SignInPage {
  case class Props(api: API, loggedIn: User => Unit, captchaKey: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "signInPage" -> CSSProperties()
          .setFlex(1)
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center")
          .setBackgroundColor("#ebeff2"),
        "signInPageFormContainer" -> CSSProperties()
          .setMaxWidth(350)
          .setWidth("100%")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val history = useHistory()

    div(className := classes("signInPage"))(
      div(className := classes("signInPageFormContainer"))(
        mui
          .Paper()
          .elevation(1)(
            Container(
              minWidth = Some("300px"),
              alignItems = Alignment.center,
              padding = EdgeInsets.all(16),
              child = Fragment(
                Title(AppStrings.signIn),
                Container(
                  flex = Some(1),
                  alignItems = Alignment.center,
                  justifyContent = Alignment.center,
                  padding = EdgeInsets.top(16),
                  child = SignInForm(props.api, props.loggedIn, props.captchaKey)
                ),
                Container(
                  margin = Container.EdgeInsets.top(8),
                  flexDirection = Container.FlexDirection.row,
                  alignItems = Container.Alignment.center,
                  justifyContent = Container.Alignment.center,
                  child = Fragment(
                    mui.Typography(AppStrings.dontHaveAccountYet),
                    mui
                      .Button(AppStrings.signUp)
                      .variant(muiStrings.text)
                      .color(muiStrings.primary)
                      .onClick(_ => history.push("/signUp"))
                  )
                ),
                Container(
                  flexDirection = Container.FlexDirection.row,
                  alignItems = Container.Alignment.center,
                  justifyContent = Container.Alignment.center,
                  child = Fragment(
                    mui.Typography(AppStrings.forgotYourPassword),
                    mui
                      .Button(AppStrings.recoverIt)
                      .variant(muiStrings.text)
                      .color(muiStrings.primary)
                      .onClick(_ => history.push("/verify-email"))
                  )
                )
              )
            )
          )
      )
    )
  }
}
