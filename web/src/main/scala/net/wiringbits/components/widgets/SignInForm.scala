package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.api.models.LoginRequest
import net.wiringbits.api.utils.Validator
import net.wiringbits.models.User
import net.wiringbits.ui.components.core.ErrorLabel
import net.wiringbits.ui.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.ui.components.core.widgets.{CircularLoader, Container, Title}
import net.wiringbits.{API, AppStrings}
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import typings.reactRouterDom.{mod => reactRouterDom}
import slinky.core.facade.ReactElement
import slinky.core.facade.Hooks
import org.scalajs.dom
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html._

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import scala.util.{Failure, Success}

@react object SignInForm {
  case class Props(api: API, loggedIn: User => Unit)

  private case class State(
      email: Option[String] = None,
      password: Option[String] = None,
      loading: Option[Boolean] = None,
      error: Option[String] = None
  )
  private val initialState = State()

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val history = reactRouterDom.useHistory()
    val (state, setState) = Hooks.useState(initialState)

    def validateForm() = {
      if (!Validator.isValidEmail(state.email.getOrElse(""))) {
        Some(AppStrings.emailAddressError)
      } else if (state.password.isEmpty) {
        Some(AppStrings.passwordRequiredError)
      } else {
        None
      }
    }

    def doLogin(e: SyntheticEvent[_, dom.Event]): Unit = {
      e.preventDefault()
      setState(
        state.copy(
          error = None,
          loading = Some(true)
        )
      )

      val email = state.email.getOrElse("")
      val password = state.password.getOrElse("")

      validateForm() match {
        case Some(validationError) =>
          setState(
            state.copy(
              error = Some(validationError),
              loading = Some(false)
            )
          )

        case None =>
          props.api.client
            .login(LoginRequest(email = email, password = password))
            .onComplete {
              case Success(res) =>
                setState(state.copy(loading = Some(false), error = None))
                props.loggedIn(User(res.name, res.email, res.token))
                history.push("/dashboard") // redirects to dashboard

              case Failure(ex) =>
                setState(
                  state.copy(
                    loading = Some(false),
                    error = Some(ex.getMessage)
                  )
                )
            }
      }
    }

    def setEmail(value: String): Unit = { setState(state.copy(email = Some(value))) }
    def setPassword(value: String): Unit = { setState(state.copy(password = Some(value))) }

    val loading = state.loading.getOrElse(false)

    val emailInput = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(8),
      child = mui
        .FormControl(
          mui.InputLabel(AppStrings.email),
          mui.Input().name("email").`type`("email").disabled(loading)
        )
        .onChange(e => setEmail(e.target.asInstanceOf[HTMLInputElement].value))
        .fullWidth(true)
    )

    val passwordInput = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(16),
      child = mui
        .FormControl(
          mui.InputLabel(AppStrings.password),
          mui.Input().name("password").`type`("password").disabled(loading)
        )
        .onChange(e => setPassword(e.target.asInstanceOf[HTMLInputElement].value))
        .fullWidth(true)
    )

    val error = ErrorLabel(state.error.getOrElse(""))

    val loginButton = {
      val text =
        if (!loading) Fragment(AppStrings.login)
        else
          Fragment(
            CircularLoader(),
            Container(margin = EdgeInsets.left(8), child = AppStrings.loading)
          )

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(loading)
        .variant(muiStrings.contained)
        .color(Color.primary)
        .`type`(muiStrings.submit)
    }

    // TODO: Use a form to get the enter key submitting the form
    form(
      onSubmit := (doLogin(_))
    )(
      mui
        .Paper()
        .elevation(1)(
          Container(
            minWidth = Some("300px"),
            alignItems = Alignment.center,
            padding = EdgeInsets.all(16),
            child = Fragment(
              Title(AppStrings.signIn),
              emailInput,
              passwordInput,
              error,
              Container(
                minWidth = Some("100%"),
                margin = EdgeInsets.top(16),
                alignItems = Alignment.center,
                child = loginButton
              )
            )
          )
        )
    )
  }
}
