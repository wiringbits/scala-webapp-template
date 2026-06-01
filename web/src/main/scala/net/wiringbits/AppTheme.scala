package net.wiringbits

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.{Theme, ThemeOptions}
import com.olvind.mui.muiMaterial.stylesMod.createTheme

import scala.scalajs.js

object AppTheme {
  val value: Theme = createTheme(
    js.Dynamic
      .literal(
        palette = js.Dynamic.literal(
          primary = js.Dynamic.literal(
            main = "#0E8C6F",
            dark = "#0A6B54",
            contrastText = "#FFFFFF"
          )
        ),
        shape = js.Dynamic.literal(borderRadius = 10),
        typography = js.Dynamic.literal(
          fontFamily =
            """-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Helvetica Neue", Arial, sans-serif""",
          button = js.Dynamic.literal(textTransform = "none", fontWeight = 600, letterSpacing = 0)
        ),
        components = js.Dynamic.literal(
          MuiButton = js.Dynamic.literal(
            defaultProps = js.Dynamic.literal(disableElevation = true),
            styleOverrides = js.Dynamic.literal(
              root = js.Dynamic.literal(
                borderRadius = 9,
                paddingLeft = 18,
                paddingRight = 18
              ),
              sizeLarge = js.Dynamic.literal(
                paddingTop = 11,
                paddingBottom = 11,
                fontSize = 15
              )
            )
          ),
          MuiOutlinedInput = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              root = js.Dynamic.literal(
                borderRadius = 10,
                backgroundColor = "#FFFFFF"
              )
            )
          ),
          MuiInputLabel = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              root = js.Dynamic.literal(fontSize = 14.5)
            )
          ),
          MuiTab = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              root = js.Dynamic.literal(textTransform = "none", fontWeight = 600, fontSize = 14, minHeight = 46)
            )
          ),
          MuiTabs = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              indicator = js.Dynamic.literal(height = 2.5, borderRadius = 2)
            )
          )
        )
      )
      .asInstanceOf[ThemeOptions]
  )
}
