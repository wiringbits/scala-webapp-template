package net.wiringbits.models

import enumeratum.{Enum, EnumEntry}

sealed abstract class UserMenuOption(val label: String) extends EnumEntry with Product with Serializable

object UserMenuOption extends Enum[UserMenuOption] {

  final case object EditSummary extends UserMenuOption("Summary")
  final case object EditPassword extends UserMenuOption("Change password")

  val values = findValues
}
