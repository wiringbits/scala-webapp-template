package net.wiringbits

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.ThemeOptions
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.stylesCreatePaletteMod.SimplePaletteColorOptions
import com.olvind.mui.muiMaterial.stylesCreatePaletteMod.PaletteOptions
import com.olvind.mui.muiMaterial.stylesCreateTypographyMod.TypographyOptions
import com.olvind.mui.muiMaterial.stylesMod.{createMuiTheme, createTheme}
import com.olvind.mui.muiMaterial.colorsMod as Colors
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.csstype.mod.Property.{BoxSizing, FlexDirection, Position}
import com.olvind.mui.muiSystem.createThemeShapeMod.ShapeOptions

object AppTheme {
  val primaryColor = Colors.teal.`500`
  val secondaryColor = Colors.amber
  val typography = TypographyOptions()
  val borderRadius = 8

  val value: Theme = createTheme(
    ThemeOptions()
      .setPalette(
        PaletteOptions()
          .setPrimary(SimplePaletteColorOptions(primaryColor))
      )
      .setTypography(typography)
      .setShape(ShapeOptions().setBorderRadius(borderRadius))
  )
}
