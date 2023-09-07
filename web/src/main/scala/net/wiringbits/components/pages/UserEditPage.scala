package net.wiringbits.components.pages

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.{EditPasswordForm, UserInfo}
import net.wiringbits.core.I18nHooks
import net.wiringbits.models.UserMenuOption.{EditPassword, EditSummary}
import net.wiringbits.models.{User, UserMenuOption}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Title}
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, KeyAddingStage}

object UserEditPage {
  def apply(ctx: AppContext, user: User): KeyAddingStage =
    component(Props(ctx = ctx, user = user))

  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val (menuOption, setMenuOption) = Hooks.useState[UserMenuOption](UserMenuOption.EditSummary)

    val header = Container(
      margin = Container.EdgeInsets.bottom(16),
      child = Title(texts.user)
    )

    val tabs = mui.CardContent()(
      mui
        .Tabs()(
          UserMenuOption.values.map(x => mui.Tab.normal().label(texts.userMenuOption(x)).withKey(x.toString).build)
        )
        .value(UserMenuOption.values.indexOf(menuOption))
        .onChange((_, index) => setMenuOption(UserMenuOption.values(index.toString.toInt)))
    )

    val body = mui.CardContent()(
      menuOption match {
        case EditSummary => UserInfo(props.ctx, props.user)
        case EditPassword => EditPasswordForm(props.ctx, props.user)
      }
    )

    Fragment(
      header,
      mui.Paper()(
        mui.Card()(
          tabs,
          body
        )
      )
    )
  }

}
