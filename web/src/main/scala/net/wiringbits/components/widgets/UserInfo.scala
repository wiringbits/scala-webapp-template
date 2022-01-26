package net.wiringbits.components.widgets

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
import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.api.utils.Formatter
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.{AppContext, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html._

import scala.util.{Failure, Success}

@react object UserInfo {
  case class Props(ctx: AppContext, user: User)

  private case class State(
      loading: Boolean,
      initialValue: Option[GetCurrentUser.Response],
      user: Option[GetCurrentUser.Response],
      hasChanges: Boolean,
      error: Option[String]
  )

  private val initialState = State(
    loading = false,
    Option.empty,
    Option.empty,
    hasChanges = false,
    Option.empty
  )

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "userEditSummaryView" -> CSSProperties()
          .setDisplay("flex")
          .set("& > *", CSSProperties().setMarginRight(16))
          .set("& > *:last-child", CSSProperties().setMarginRight(0)),
        "section" -> CSSProperties()
          .setFlex(1)
          .setDisplay("flex")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (state, setState) = Hooks.useState(initialState)
    val classes = useStyles(())

    def fetchUserData(): Unit = {
      setState(_.copy(loading = true))
      props.ctx.api.client.currentUser(props.user.jwt).onComplete {
        case Success(res) =>
          setState(_.copy(loading = false, user = Some(res), initialValue = Some(res)))
        case Failure(ex) =>
          setState(_.copy(loading = false, error = Some(ex.toString)))
      }
    }

    Hooks.useEffect(() => fetchUserData(), "")

    def loader = Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = Fragment(
        CircularLoader(48),
        mui.Typography(AppStrings.loading).variant(muiStrings.h4).color(muiStrings.primary)
      )
    )

    def onSaveClick(): Unit = {
      fetchUserData()
      state.user.foreach { user =>
        renderBody(user)
      }
    }

    def renderBody(user: GetCurrentUser.Response) = {
      div(className := classes("userEditSummaryView"))(
        div(className := classes("section"))(EditUserForm(props.ctx, props.user, user, onSaveClick)),
        div(className := classes("section"))(
          div()(
            mui.Typography("Created at").variant(muiStrings.subtitle2),
            mui.Typography(Formatter.instant(user.createdAt))
          )
        )
      )
    }

    if (state.loading) {
      loader
    } else {
      state.user match {
        case Some(user) => renderBody(user)
        case None => div("User not found")
      }
    }
  }
}
