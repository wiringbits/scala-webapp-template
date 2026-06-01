package net.wiringbits.components.pages

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.{EditPasswordForm, UserInfo}
import net.wiringbits.core.I18nHooks
import net.wiringbits.models.UserMenuOption.{EditPassword, EditSummary}
import net.wiringbits.models.{User, UserMenuOption}
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object UserEditPage {
  def apply(ctx: AppContext, user: User): KeyAddingStage =
    component(Props(ctx = ctx, user = user))

  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val (menuOption, setMenuOption) = Hooks.useState[UserMenuOption](UserMenuOption.EditSummary)

    Fragment(
      mui
        .Typography(texts.user)
        .variant("h3")
        .sx(
          js.Dynamic
            .literal(
              fontSize = 34,
              fontWeight = 700,
              letterSpacing = "-0.02em",
              marginBottom = "32px"
            )
            .asInstanceOf[CSSPropertiesUtils]
        ),
      mui
        .Card()
        .variant("outlined")
        .sx(
          js.Dynamic
            .literal(borderRadius = "12px", borderColor = "#E6ECEA", maxWidth = 720)
            .asInstanceOf[CSSPropertiesUtils]
        )(
          mui
            .Tabs()(
              UserMenuOption.values.map(x => mui.Tab.normal().label(texts.userMenuOption(x)).withKey(x.toString).build)
            )
            .value(UserMenuOption.values.indexOf(menuOption))
            .onChange((_, index) => setMenuOption(UserMenuOption.values(index.toString.toInt)))
            .sx(
              js.Dynamic
                .literal(
                  px = "8px",
                  borderBottom = "1px solid #E6ECEA"
                )
                .asInstanceOf[CSSPropertiesUtils]
            ),
          mui
            .CardContent()
            .sx(js.Dynamic.literal(padding = "32px").asInstanceOf[CSSPropertiesUtils])(
              menuOption match {
                case EditSummary => UserInfo(props.ctx, props.user)
                case EditPassword => EditPasswordForm(props.ctx, props.user)
              }
            )
        )
    )
  }
}
