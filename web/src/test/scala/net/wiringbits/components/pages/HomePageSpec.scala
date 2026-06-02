package net.wiringbits.components.pages

import monix.execution.Scheduler
import monix.reactive.subjects.Var
import net.wiringbits.common.models.Email
import net.wiringbits.core.I18nLang
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.BrowserRouter
import net.wiringbits.{API, AppContext}
import org.scalajs.dom
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import scala.compiletime.uninitialized
import slinky.core.facade.ReactElement
import slinky.web.ReactDOM

class HomePageSpec extends AnyWordSpec with BeforeAndAfterEach {

  private var container: dom.Element = uninitialized

  override def beforeEach(): Unit = {
    container = dom.document.createElement("div")
    dom.document.body.appendChild(container)
  }

  override def afterEach(): Unit = {
    ReactDOM.unmountComponentAtNode(container)
    dom.document.body.removeChild(container)
  }

  // Minimal context: HomePage only reads $auth, $darkMode, $lang — api is never touched.
  private def testCtx(): AppContext = {
    given Scheduler = Scheduler.trampoline()
    AppContext(
      api = null.asInstanceOf[API],
      $auth = Var[AuthState](AuthState.Unauthenticated),
      $lang = Var[I18nLang](I18nLang.English),
      $darkMode = Var[Boolean](false),
      contactEmail = Email.trusted("test@test.com"),
      contactPhone = "+1 555 555 5555",
      executionContext = scala.concurrent.ExecutionContext.global
    )
  }

  private def render(ctx: AppContext): Unit =
    ReactDOM.render(
      BrowserRouter(basename = "")(HomePage(ctx): ReactElement),
      container
    )

  "HomePage" should {

    // This test catches: object-level vals storing KeyAddingStage, or any val inside
    // the render function that is placed into the tree more than once in one pass.
    "render without component-reuse errors (first render)" in {
      noException should be thrownBy render(testCtx())
    }

    // This test catches: any KeyAddingStage that survives between renders (e.g. a val
    // captured in a closure or stored in a companion object).
    "render without component-reuse errors on re-render (dark mode toggle)" in {
      val ctx = testCtx()
      render(ctx)
      noException should be thrownBy {
        ctx.$darkMode := true
        render(ctx)
      }
    }

    "render without component-reuse errors on re-render (auth state change)" in {
      val ctx = testCtx()
      render(ctx)
      noException should be thrownBy {
        ctx.$auth := AuthState.Unauthenticated
        render(ctx)
      }
    }
  }
}
