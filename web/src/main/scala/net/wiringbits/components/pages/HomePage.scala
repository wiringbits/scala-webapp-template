package net.wiringbits.components.pages

import com.alexitc.materialui.facade.csstype.mod.TextAlignProperty
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
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.Alignment
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets._
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html._

@react object HomePage {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "homeContainer" -> CSSProperties()
          .setMaxWidth(1300)
          .setWidth("100%"),
        "homeTitle" -> CSSProperties()
          .setTextAlign(TextAlignProperty.center)
          .setMargin("8px 0"),
        "snippet" -> CSSProperties()
          .setMaxWidth(800)
          .setWidth("100%")
          .setDisplay("block")
          .setMargin("1em auto"),
        "screenshot" -> CSSProperties()
          .setMaxWidth(1200)
          .setWidth("100%")
          .setDisplay("block")
          .setMargin("1em auto")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val classes = useStyles(())

    def title(msg: String) = mui
      .Typography(msg)
      .variant(muiStrings.h4)
      .color(muiStrings.inherit)

    def paragraph(args: ReactElement) = mui
      .Typography(args)
      .variant(muiStrings.body1)
      .color(muiStrings.inherit)

    def link(msg: String, url: String) = mui
      .Link(msg)
      .href(url)
      .target("_blank")

    def image(srcImg: String, altImg: String, classImg: String) =
      img(src := srcImg, alt := altImg, className := classes(classImg))

    val homeFragment = Fragment(
      mui
        .Typography(texts.homePage, className := classes("homeTitle"))
        .variant(muiStrings.h4)
        .color(muiStrings.inherit),
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

    val adminPortalFragment = Fragment(
      title(texts.easilyExposeDataAdminPortal),
      paragraph(
        Fragment(
          texts.easilyExposeDataAdminPortalDescriptionStart,
          link(texts.reactAdmin, "https://marmelab.com/react-admin/"),
          texts.easilyExposeDataAdminPortalDescriptionEnd.toLowerCase
        )
      ),
      paragraph(texts.thisSnippet),
      image("/img/home/admin-user-table-snippet.png", texts.adminUserTableSnippet, "snippet"),
      paragraph(texts.rendersAUserList),
      image("/img/home/admin-user-list.png", texts.adminUserList, "screenshot"),
      paragraph(texts.allowsViewingUpdatingASingleUser),
      image("/img/home/admin-user-view.png", texts.adminUserView, "screenshot"),
      paragraph(
        Fragment(
          paragraph(
            Fragment(
              link(texts.tryIt, "https://template-demo-admin.wiringbits.net"),
              " (user=",
              strong("demo"),
              ", password=",
              strong("wiringbits"),
              ")"
            )
          )
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
      alignItems = Alignment.center,
      child = div(className := classes("homeContainer"))(
        Fragment(
          homeFragment,
          userProfileFragment,
          adminPortalFragment,
          swaggerFragment,
          dataLoadingFragment,
          simpleArchitectureFragment
        )
      )
    )
  }
}
