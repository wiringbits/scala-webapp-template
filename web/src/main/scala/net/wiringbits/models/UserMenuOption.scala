package net.wiringbits.models

import enumeratum.{Enum, EnumEntry}

sealed abstract class UserMenuOption extends EnumEntry with Product with Serializable

object UserMenuOption extends Enum[UserMenuOption] {

  case object EditSummary extends UserMenuOption
  case object EditPassword extends UserMenuOption

  val values = findValues
}
