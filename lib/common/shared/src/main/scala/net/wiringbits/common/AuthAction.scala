package net.wiringbits.common

import java.util.UUID

enum AuthAction {
  case RemoveSession
  case SetSession(userId: UUID)
}
