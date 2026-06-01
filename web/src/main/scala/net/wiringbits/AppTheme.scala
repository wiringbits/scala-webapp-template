package net.wiringbits

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.{Theme, ThemeOptions}
import com.olvind.mui.muiMaterial.stylesMod.createTheme

import scala.scalajs.js

object AppTheme {
  val light: Theme = createTheme(
    js.Dynamic
      .literal(
        palette = js.Dynamic.literal(
          primary = js.Dynamic.literal(
            main = "#0E8C6F",
            dark = "#0A6B54",
            contrastText = "#FFFFFF"
          ),
          success = js.Dynamic.literal(main = "#0E8C6F"),
          error = js.Dynamic.literal(main = "#C0392B"),
          background = js.Dynamic.literal(
            default = "#F4F7F6",
            paper = "#FFFFFF"
          ),
          text = js.Dynamic.literal(
            primary = "#15201D",
            secondary = "#5A6A65"
          ),
          divider = "#E6ECEA"
        ),
        shape = js.Dynamic.literal(borderRadius = 10),
        typography = js.Dynamic.literal(
          fontFamily =
            """-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Helvetica Neue", Arial, sans-serif""",
          h1 = js.Dynamic.literal(fontWeight = 800, letterSpacing = "-0.025em", lineHeight = 1.05),
          h2 = js.Dynamic.literal(fontWeight = 800, letterSpacing = "-0.02em"),
          h3 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.02em"),
          h4 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.015em"),
          h5 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.01em"),
          h6 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.01em"),
          overline = js.Dynamic.literal(fontWeight = 700, letterSpacing = "0.12em", fontSize = 11.5),
          button = js.Dynamic.literal(textTransform = "none", fontWeight = 600, letterSpacing = 0)
        ),
        components = js.Dynamic.literal(
          MuiCssBaseline = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              body = js.Dynamic.literal(backgroundColor = "#F4F7F6")
            )
          ),
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

  val darkTheme: Theme = createTheme(
    js.Dynamic
      .literal(
        palette = js.Dynamic.literal(
          mode = "dark",
          primary = js.Dynamic.literal(
            main = "#2FCFA6",
            dark = "#23A584",
            contrastText = "#06140F"
          ),
          success = js.Dynamic.literal(main = "#34D399"),
          error = js.Dynamic.literal(main = "#F87171"),
          background = js.Dynamic.literal(
            default = "#0B100F",
            paper = "#141B19"
          ),
          text = js.Dynamic.literal(
            primary = "#E9EEEC",
            secondary = "#93A29D"
          ),
          divider = "#222D2A"
        ),
        shape = js.Dynamic.literal(borderRadius = 10),
        typography = js.Dynamic.literal(
          fontFamily =
            """-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Helvetica Neue", Arial, sans-serif""",
          h1 = js.Dynamic.literal(fontWeight = 800, letterSpacing = "-0.025em", lineHeight = 1.05),
          h2 = js.Dynamic.literal(fontWeight = 800, letterSpacing = "-0.02em"),
          h3 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.02em"),
          h4 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.015em"),
          h5 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.01em"),
          h6 = js.Dynamic.literal(fontWeight = 700, letterSpacing = "-0.01em"),
          overline = js.Dynamic.literal(fontWeight = 700, letterSpacing = "0.12em", fontSize = 11.5),
          button = js.Dynamic.literal(textTransform = "none", fontWeight = 600, letterSpacing = 0)
        ),
        components = js.Dynamic.literal(
          MuiCssBaseline = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              body = js.Dynamic.literal(backgroundColor = "#0B100F")
            )
          ),
          MuiPaper = js.Dynamic.literal(
            styleOverrides = js.Dynamic.literal(
              root = js.Dynamic.literal(backgroundImage = "none")
            )
          ),
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
              root = js.Dynamic.literal(borderRadius = 10)
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
