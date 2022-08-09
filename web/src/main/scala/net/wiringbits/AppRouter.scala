package net.wiringbits

import net.wiringbits.components.pages._
import net.wiringbits.components.widgets.{AppBar, Footer}
import net.wiringbits.core.ReactiveHooks
import net.wiringbits.models.{AuthState, User}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Scaffold
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.components.Route
import typings.reactRouterDom.{components => router}

import scala.util.{Failure, Success}

@react object AppRouter {
  case class Props(ctx: AppContext)

  private def route(path: String, ctx: AppContext)(child: => ReactElement): Route.Builder[RouteProps] = {
    router.Route(
      RouteProps()
        .setExact(true)
        .setPath(path)
        .setRender { route =>
          Scaffold(
            appbar = Some(AppBar(ctx)),
            body = child,
            footer = Some(Footer(ctx))
          )
        }
    )
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    import props.ctx.executionContext

    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val home = route("/", props.ctx)(HomePage(props.ctx))
    val about = route("/about", props.ctx)(AboutPage(props.ctx))
    val signIn = route("/signin", props.ctx)(SignInPage(props.ctx))
    val signUp = route("/signup", props.ctx)(SignUpPage(props.ctx))
    val email = route("/verify-email", props.ctx)(VerifyEmailPage(props.ctx))
    val emailCode = route("/verify-email/:emailCode", props.ctx)(VerifyEmailWithTokenPage(props.ctx))
    val forgotPassword = route("/forgot-password", props.ctx)(ForgotPasswordPage(props.ctx))
    val resetPassword = route("/reset-password/:resetPasswordCode", props.ctx)(ResetPasswordPage(props.ctx))
    val resendVerifyEmail = route("/resend-verify-email", props.ctx)(ResendVerifyEmailPage(props.ctx))

    def dashboard(user: User) = route("/dashboard", props.ctx)(DashboardPage(props.ctx, user))
    def me(user: User) = route("/me", props.ctx)(UserEditPage(props.ctx, user))
    val signOut = route("/signout", props.ctx) {
      props.ctx.api.client.logout().onComplete {
        case Success(_) =>
          props.ctx.loggedOut()
          println("Logged out successfully")

        case Failure(exception) =>
          println(s"Failed to log out: ${exception.getMessage}")
      }

      router.Redirect("/")
    }

    val catchAllRoute = router.Route(
      RouteProps().setRender { _ =>
        router.Redirect("/")
      }
    )

    auth match {
      case AuthState.Unauthenticated =>
        router.Switch(
          home,
          about,
          signIn,
          signUp,
          email,
          emailCode,
          forgotPassword,
          resetPassword,
          resendVerifyEmail,
          catchAllRoute
        )

      case AuthState.Authenticated(user) =>
        router.Switch(home, me(user), dashboard(user), about, signOut, catchAllRoute)
    }
  }
}
