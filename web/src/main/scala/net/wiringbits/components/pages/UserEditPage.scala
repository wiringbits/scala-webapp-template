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
import net.wiringbits.api.models.{GetCurrentUser, UpdatePassword, UpdateUser}
import net.wiringbits.common.models.Password
import net.wiringbits.components.widgets.{UserEditPasswordView, UserEditSummaryView}
import net.wiringbits.models.UserMenuOption.{EditPassword, EditSummary}
import net.wiringbits.models.{User, UserMenuOption}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container, Title}
import net.wiringbits.{API, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html.{className, div}

import scala.util.{Failure, Success}

@react object UserEditPage {
  case class Props(api: API, user: User)

  private case class State(
      loading: Boolean,
      menuOption: UserMenuOption,
      initialValue: Option[GetCurrentUser.Response],
      user: Option[GetCurrentUser.Response],
      hasChanges: Boolean,
      password: Option[Password],
      hasPasswordChanges: Boolean,
      error: Option[String]
  )

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "userPageBody" -> CSSProperties()
          .setDisplay("flex"),
        "userSection" -> CSSProperties()
          .setFlex(1)
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("center"),
        "actions" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("flex-end")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  private val initialState = State(
    loading = false,
    UserMenuOption.EditSummary,
    Option.empty,
    Option.empty,
    hasChanges = false,
    Option.empty,
    hasPasswordChanges = false,
    Option.empty
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (state, setState) = Hooks.useState(initialState)

    def fetchUserData(): Unit = {
      setState(_.copy(loading = true))
      props.api.client.currentUser(props.user.jwt).onComplete {
        case Success(res) =>
          setState(_.copy(loading = false, user = Some(res), initialValue = Some(res)))
        case Failure(ex) =>
          setState(_.copy(loading = false, error = Some(ex.toString)))
      }
    }

    def onPasswordChange(updatedPassword: String): Unit = {
      val password = Password.validate(updatedPassword).toOption
      setState(_.copy(password = password, hasPasswordChanges = password.isDefined))
    }

    def onUserValueChange(userUpdated: GetCurrentUser.Response): Unit = {
      val initialValue = state.initialValue.get
      val hasChanges = initialValue.name != userUpdated.name

      setState(
        _.copy(
          user = Some(userUpdated),
          hasChanges = hasChanges
        )
      )
    }

    def onSaveClick(): Unit = {
      setState(_.copy(loading = true))

      state.user.foreach { userUpdated =>
        val request = UpdateUser.Request(userUpdated.name)

        props.api.client.updateUser(props.user.jwt, request).onComplete {
          case Success(_) =>
            setState(_.copy(loading = false, hasChanges = false))
          case Failure(ex) =>
            setState(_.copy(loading = false, error = Some(ex.toString)))
        }
      }
    }

    def onPasswordSave(): Unit = {
      state.password.foreach { password =>
        val request = UpdatePassword.Request(password)

        props.api.client.updatePassword(props.user.jwt, request).onComplete {
          case Success(_) =>
            setState(_.copy(loading = false, hasChanges = false, password = None))
          case Failure(ex) =>
            setState(_.copy(loading = false, error = Some(ex.toString), password = None))
        }
      }
    }

    Hooks.useEffect(() => fetchUserData(), "")

    val header = Container(
      margin = Container.EdgeInsets.bottom(16),
      child = Title(AppStrings.user)
    )

    def loader = Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = Fragment(
        CircularLoader(48),
        mui.Typography(AppStrings.loading).variant(muiStrings.h4).color(muiStrings.primary)
      )
    )

    def renderBody(user: GetCurrentUser.Response) = {
      val tabs = mui.CardContent()(
        mui
          .Tabs(UserMenuOption.values.indexOf(state.menuOption))(
            UserMenuOption.values.map(x => mui.Tab().label(x.label).withKey(x.label))
          )
          .onChange((_, index) => setState(_.copy(menuOption = UserMenuOption.values(index.toString.toInt))))
      )

      val body = mui.CardContent()(
        state.menuOption match {
          case EditSummary => UserEditSummaryView(user, onUserValueChange)
          case EditPassword =>
            UserEditPasswordView(state.password.map(_.string).getOrElse(""), onChange = onPasswordChange)
        }
      )

      val actions = mui.CardActions(className := classes("actions"))(
        state.menuOption match {
          case EditSummary =>
            mui
              .Button()("Save")
              .variant(muiStrings.contained)
              .color(muiStrings.primary)
              .disabled(!state.hasChanges)
              .onClick(_ => onSaveClick())
          case EditPassword =>
            mui
              .Button()("Save password")
              .variant(muiStrings.contained)
              .color(muiStrings.primary)
              .disabled(!state.hasPasswordChanges)
              .onClick(_ => onPasswordSave())
        }
      )

      mui.Card()(
        tabs,
        body,
        actions
      )
    }

    Fragment(
      header,
      if (state.loading) {
        loader
      } else {
        mui.Paper()(
          state.user match {
            case Some(user) => renderBody(user)
            case None => div("User not found")
          }
        )
      }
    )
  }

}
