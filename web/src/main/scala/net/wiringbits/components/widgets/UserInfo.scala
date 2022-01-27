package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object UserInfo {
  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    def loader = Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = Fragment(
        CircularLoader(48),
        mui.Typography(texts.loading).variant(muiStrings.h4).color(muiStrings.primary)
      )
    )

    def onSaveClick(): Unit = {
      renderBody()
    }

    def renderBody() = {
      val form = {
        AsyncComponent.component[GetCurrentUser.Response](
          AsyncComponent.Props(
            fetch = () => props.ctx.api.client.currentUser(props.user.jwt),
            render =
              response => EditUserForm.component(EditUserForm.Props(props.ctx, props.user, response, onSaveClick)),
            progressIndicator = () => loader
          )
        )
      }

      form
    }

    renderBody()
  }
}
