package net.wiringbits.components.pages

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets._
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html._

@react object HomePage {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    // TODO: add styles to titles, texts and images
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    def link(msg: String, url: String) =
      mui.Link(msg).href(url).target("_blank")

    def image(srcImg: String, altImg: String) =
      img(src := srcImg, alt := altImg)

    val homeFragment = Fragment(
      Title(texts.homePage),
      mui.Typography(texts.homePageDescription)
    )

    val userProfileFragment = Fragment(
      Title(texts.userProfile),
      mui.Typography(
        texts.userProfileDescription,
        link(texts.tryIt.toLowerCase, "https://template-demo.wiringbits.net/signin")
      )
    )

    val adminPortalFragment = Fragment(
      Title(texts.easilyExposeDataAdminPortal),
      mui.Typography(
        texts.easilyExposeDataAdminPortalDescriptionStart,
        link(texts.reactAdmin, "https://marmelab.com/react-admin/"),
        texts.easilyExposeDataAdminPortalDescriptionEnd.toLowerCase
      ),
      mui.Typography(texts.thisSnippet),
      image("/img/home/admin-user-table-snippet.png", texts.adminUserTableSnippet),
      mui.Typography(texts.rendersAUserList),
      image("/img/home/admin-user-list.png", texts.adminUserList),
      mui.Typography(texts.allowsViewingUpdatingASingleUser),
      image("/img/home/admin-user-view.png", texts.adminUserView),
      mui.Typography(link(texts.tryIt, "https://template-demo-admin.wiringbits.net"), texts.adminUserPassword)
    )

    val swaggerFragment = Fragment(
      Title(texts.swaggerIntegration),
      mui.Typography(
        texts.swaggerIntegrationDescription,
        link(texts.tryIt.toLowerCase, "https://template-demo.wiringbits.net/api/docs/index.html")
      ),
      image("/img/home/swagger.png", texts.swaggerIntegration)
    )

    val dataLoadingFragment = Fragment(
      Title(texts.consistentDataLoading),
      mui.Typography(texts.consistentDataLoadingDescription),
      image("/img/home/async-component-snippet.png", texts.swaggerIntegration),
      mui.Typography(texts.dataIsBeingLoaded),
      image("/img/home/async-progress.png", texts.swaggerIntegration),
      mui.Typography(texts.problemFetchingData),
      image("/img/home/async-retry.png", texts.swaggerIntegration)
    )

    val simpleArchitectureFragment = Fragment(
      Title(texts.simpleToFollowArchitecture),
      mui.Typography(texts.simpleToFollowArchitectureDescription1),
      mui.Typography(texts.simpleToFollowArchitectureDescription2)
    )

    Container(
      child = Fragment(
        homeFragment,
        userProfileFragment,
        adminPortalFragment,
        swaggerFragment,
        dataLoadingFragment,
        simpleArchitectureFragment
      )
    )
  }
}
