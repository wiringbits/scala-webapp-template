package net.wiringbits.components.pages

import com.olvind.mui.csstype.mod.Property.TextAlign
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.*
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.*

object HomePage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  private val homeContainerStyling = new CSSPropertiesUtils {
    maxWidth = 1300
    width = "100%"
  }

  private val homeTitleStyling = new CSSPropertiesUtils {
    textAlign = TextAlign.center
    margin = "8px 0"

  }

  private val screenshotStyling = new CSSPropertiesUtils {
    maxWidth = 1200
    width = "100%"
    display = "block"
    margin = "1em auto"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    def title(msg: String) = mui
      .Typography(msg)
      .variant("h4")
      .color("inherit")

    def paragraph(args: ReactElement) = mui
      .Typography(args)
      .variant("body1")
      .color("inherit")

    def link(msg: String, url: String) = mui
      .Link(msg)
      .href(url)
      .target("_blank")

    def image(srcImg: String, altImg: String, classImg: String) =
      img(src := srcImg, alt := altImg, className := classImg, style := screenshotStyling)

    val homeFragment = Fragment(
      mui
        .Typography(texts.homePage, className := "homeTitle", style := homeTitleStyling)
        .variant("h4")
        .color("inherit"),
      paragraph(texts.homePageDescription),
      br(),
      br()
    )

    val userProfileFragment = Fragment(
      title(texts.userProfile),
      paragraph(
        Fragment(
          texts.userProfileDescription,
          link(texts.tryIt.toLowerCase, "https://template-demo.wiringbits.net/signin")
        )
      ),
      br(),
      br()
    )

    val swaggerFragment = Fragment(
      title(texts.swaggerIntegration),
      paragraph(
        Fragment(
          texts.swaggerIntegrationDescription,
          link(texts.tryIt.toLowerCase, "https://template-demo.wiringbits.net/api/docs/index.html")
        )
      ),
      image("/img/home/swagger.png", texts.swaggerIntegration, "screenshot"),
      br(),
      br()
    )

    val dataLoadingFragment = Fragment(
      title(texts.consistentDataLoading),
      paragraph(texts.consistentDataLoadingDescription),
      image("/img/home/async-component-snippet.png", texts.swaggerIntegration, "snippet"),
      paragraph(texts.dataIsBeingLoaded),
      image("/img/home/async-progress.png", texts.swaggerIntegration, "screenshot"),
      paragraph(texts.problemFetchingData),
      image("/img/home/async-retry.png", texts.swaggerIntegration, "screenshot"),
      br(),
      br()
    )

    val simpleArchitectureFragment = Fragment(
      title(texts.simpleToFollowArchitecture),
      paragraph(texts.simpleToFollowArchitectureDescription1),
      paragraph(texts.simpleToFollowArchitectureDescription2),
      br(),
      br()
    )

    Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      child = mui.Box.sx(homeContainerStyling)(
        homeFragment,
        userProfileFragment,
        swaggerFragment,
        dataLoadingFragment,
        simpleArchitectureFragment
      )
    )
  }
}
