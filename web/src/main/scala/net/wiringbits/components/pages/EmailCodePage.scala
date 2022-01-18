package net.wiringbits.components.pages

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, TextAlignProperty}
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
import net.wiringbits.api.models.VerifyEmailRequest
import net.wiringbits.ui.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.{API, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html.{className, div}
import typings.reactRouterDom.mod.{useHistory, useParams}

import scala.scalajs.js
import scala.scalajs.js.timers.setTimeout
import scala.util.{Failure, Success}

@react object EmailCodePage {
  case class Props(api: API)

  private case class State(
      loading: Boolean,
      error: Option[String],
      title: String,
      message: String
  )

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "emailCodePage" -> CSSProperties()
          .setFlex(1)
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setTextAlign(TextAlignProperty.center)
          .setJustifyContent("center"),
        "emailTitle" -> CSSProperties()
          .setFontWeight(600),
        "emailPhoto" -> CSSProperties()
          .setWidth("15rem")
          .setPadding("15px 0")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  private val initialState = State(
    loading = false,
    error = None,
    title = AppStrings.verifyingEmail,
    message = AppStrings.waitAMomentPlease
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val history = useHistory()
    val params = useParams()
    val (state, setState) = Hooks.useState(initialState)
    val emailCode = params.asInstanceOf[js.Dynamic].emailCode.toString

    def sendEmailCode(): Unit = {
      setState(_.copy(loading = true))
      props.api.client.verifyEmail(VerifyEmailRequest(emailCode)).onComplete {
        case Success(_) =>
          val title = AppStrings.successfulEmailVerification
          val message = AppStrings.goingToBeRedirected
          setState(_.copy(loading = false, title = title, message = message))
          setTimeout(2000) {
            history.push("/signin")
          }

        case Failure(ex) =>
          val title = AppStrings.failedEmailVerification
          val message = ex.getMessage
          setState(_.copy(loading = false, title = title, message = message, error = Some(ex.getMessage)))
      }
    }

    Hooks.useEffect(() => sendEmailCode(), "")

    val loading =
      if (state.loading || state.error.isEmpty)
        Fragment(
          loader
        )
      else {
        Fragment(
        )
      }

    div(className := classes("emailCodePage"))(
      Fragment(
        mui.Typography(state.title).variant(muiStrings.h5).className(classes("emailTitle")),
        mui.Typography(state.message).variant(muiStrings.h6),
        loading
      )
    )
  }

  private def loader = Container(
    alignItems = Container.Alignment.center,
    justifyContent = Container.Alignment.center,
    padding = Container.EdgeInsets.vertical(16),
    child = Fragment(
      CircularLoader(50)
    )
  )
}
