package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
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
import net.wiringbits.AppStrings
import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.common.models.{Email, Name}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._

@react object UserEditForm {
  case class Props(user: GetCurrentUser.Response, onChange: GetCurrentUser.Response => Unit)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "userEditForm" -> CSSProperties()
          .setWidth("100%")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column),
        "input" -> CSSProperties()
          .setMinWidth(200)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (formData, setFormData) = Hooks.useState(props.user)

    def onChange(value: GetCurrentUser.Response): Unit = {
      setFormData(value)
      props.onChange(value)
    }

    val nameInput = OutlinedTextInput(
      AppStrings.name,
      formData.name.string,
      value => onChange(formData.copy(name = Name.trusted(value)))
    )

    val emailInput =
      OutlinedTextInput(
        AppStrings.email,
        formData.email.string,
        value => onChange(formData.copy(email = Email.trusted(value))),
        disabled = true
      )

    div(className := classes("userEditForm"))(
      nameInput,
      emailInput
    )
  }
}
