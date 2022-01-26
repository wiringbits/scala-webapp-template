package net.wiringbits.components.pages

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import net.wiringbits.components.widgets.{EditPasswordForm, UserInfo}
import net.wiringbits.models.UserMenuOption.{EditPassword, EditSummary}
import net.wiringbits.models.{User, UserMenuOption}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Title}
import net.wiringbits.{API, AppStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}

@react object UserEditPage {
  case class Props(api: API, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (menuOption, setMenuOption) = Hooks.useState[UserMenuOption](UserMenuOption.EditSummary)

    val header = Container(
      margin = Container.EdgeInsets.bottom(16),
      child = Title(AppStrings.user)
    )

    val tabs = mui.CardContent()(
      mui
        .Tabs(UserMenuOption.values.indexOf(menuOption))(
          UserMenuOption.values.map(x => mui.Tab().label(x.label).withKey(x.label))
        )
        .onChange((_, index) => setMenuOption(UserMenuOption.values(index.toString.toInt)))
    )

    val body = mui.CardContent()(
      menuOption match {
        case EditSummary => UserInfo(props.api, props.user)
        case EditPassword =>
          EditPasswordForm(props.api, props.user)
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
