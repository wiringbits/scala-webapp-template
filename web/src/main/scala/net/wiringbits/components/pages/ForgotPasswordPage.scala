package net.wiringbits.components.pages

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.components.widgets.ForgotPasswordForm
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.{API, AppStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import typings.reactRouterDom.mod.useHistory

@react object ForgotPasswordPage {
  case class Props(api: API, captchaKey: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val history = useHistory()

    Fragment(
      ForgotPasswordForm(props.api, props.captchaKey),
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
      )
    )
  }

}
