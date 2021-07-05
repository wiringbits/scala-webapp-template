package net.wiringbits

import com.alexitc.materialui.facade.csstype.mod.{BoxSizingProperty, FlexDirectionProperty, PositionProperty}
import com.alexitc.materialui.facade.materialUiCore.anon.{PartialPaperProps, PartialStyleRulesPaperCla}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.{Theme, ThemeOptions}
import com.alexitc.materialui.facade.materialUiCore.createPaletteMod.{PaletteColorOptions, PaletteOptions}
import com.alexitc.materialui.facade.materialUiCore.createTypographyMod.TypographyOptions
import com.alexitc.materialui.facade.materialUiCore.overridesMod.Overrides
import com.alexitc.materialui.facade.materialUiCore.propsMod.ComponentsProps
import com.alexitc.materialui.facade.materialUiCore.shapeMod.ShapeOptions
import com.alexitc.materialui.facade.materialUiCore.stylesMod.createMuiTheme
import com.alexitc.materialui.facade.materialUiCore.withStylesMod.CSSProperties
import com.alexitc.materialui.facade.materialUiCore.{colorsMod => Colors}

object AppTheme {
  val primaryColor = Colors.teal.`500`
  val secondaryColor = Colors.amber
  val typography = TypographyOptions().setUseNextVariants(true)
  val borderRadius = 8

  val value: Theme = createMuiTheme(
    ThemeOptions()
      .setPalette(
        PaletteOptions()
          .setPrimary(PaletteColorOptions.SimplePaletteColorOptions(primaryColor))
      )
      .setTypography(typography)
      .setShape(ShapeOptions().setBorderRadius(borderRadius))
      .setProps(
        ComponentsProps()
          .setMuiPaper(
            PartialPaperProps()
              .setElevation(1)
          )
      )
      .setOverrides(
        Overrides()
          .setMuiPaper(
            PartialStyleRulesPaperCla()
              .setRoot(
                CSSProperties()
                  .setPosition(PositionProperty.relative)
                  .setDisplay("flex")
                  .setFlexDirection(FlexDirectionProperty.column)
                  .setBoxSizing(BoxSizingProperty.`border-box`)
                  .setOverflow("hidden")
              )
              .setRounded(CSSProperties().setBorderRadius(borderRadius))
          )
      )
  )
}
