package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.api.models.auth.GetCurrentUser
import net.wiringbits.core.I18nHooks
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

object UserInfo {
  def apply(ctx: AppContext, user: User): KeyAddingStage =
    component(Props(ctx = ctx, user = user))

  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    def loader = Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = Fragment(
        CircularLoader(48),
        mui.Typography(texts.loading).variant("h4").color("primary")
      )
    )

    def onSaveClick(): Unit = {
      renderBody()
    }

    def renderBody() = {
      AsyncComponent[GetCurrentUser.Response](
        fetch = () => props.ctx.api.client.currentUser,
        render = response => EditUserForm(props.ctx, props.user, response, onSaveClick),
        progressIndicator = () => loader
      )
    }

    renderBody()
  }
}
