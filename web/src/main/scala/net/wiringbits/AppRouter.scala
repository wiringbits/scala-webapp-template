package net.wiringbits

import net.wiringbits.components.pages.*
import net.wiringbits.components.widgets.{AppBar, Footer}
import net.wiringbits.core.ReactiveHooks
import net.wiringbits.models.{AuthState, User}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Scaffold
import slinky.core.FunctionalComponent
import slinky.core.facade.ReactElement
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.components as router
import typings.reactRouterDom.components.Route

import scala.util.{Failure, Success}

object AppRouter {
  case class Props(ctx: AppContext)

  private def route(path: String, ctx: AppContext)(child: => ReactElement): Route.Builder[RouteProps] = {
    router.Route(
      RouteProps()
        .setExact(true)
        .setPath(path)
        .setRender { route =>
          Scaffold(
            appbar = Some(AppBar.component(AppBar.Props(ctx))),
            body = child,
            footer = Some(Footer.component(Footer.Props(ctx)))
          )
        }
    )
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    implicit val ec = props.ctx.executionContext
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val home = route("/", props.ctx)(HomePage.component(HomePage.Props(props.ctx)))
    val about = route("/about", props.ctx)(AboutPage.component(AboutPage.Props(props.ctx)))
    val signIn = route("/signin", props.ctx)(SignInPage.component(SignInPage.Props(props.ctx)))
    val signUp = route("/signup", props.ctx)(SignUpPage.component(SignUpPage.Props(props.ctx)))
    val email = route("/verify-email", props.ctx)(VerifyEmailPage.component(VerifyEmailPage.Props(props.ctx)))
    val emailCode = route("/verify-email/:emailCode", props.ctx)(
      VerifyEmailWithTokenPage.component(VerifyEmailWithTokenPage.Props(props.ctx))
    )
    val forgotPassword =
      route("/forgot-password", props.ctx)(ForgotPasswordPage.component(ForgotPasswordPage.Props(props.ctx)))
    val resetPassword = route("/reset-password/:resetPasswordCode", props.ctx)(
      ResetPasswordPage.component(ResetPasswordPage.Props(props.ctx))
    )
    val resendVerifyEmail =
      route("/resend-verify-email", props.ctx)(ResendVerifyEmailPage.component(ResendVerifyEmailPage.Props(props.ctx)))

    def dashboard(user: User) =
      route("/dashboard", props.ctx)(DashboardPage.component(DashboardPage.Props(props.ctx, user)))
    def me(user: User) = route("/me", props.ctx)(UserEditPage.component(UserEditPage.Props(props.ctx, user)))
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
