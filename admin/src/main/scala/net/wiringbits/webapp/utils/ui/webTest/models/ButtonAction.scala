package net.wiringbits.webapp.utils.ui.webTest.models

import scala.concurrent.Future

/** @param text
  *   button label that's going to be displayed in react-admin
  * @param onClick
  *   this callback has the resource ID of the clicked element
  */
case class ButtonAction(text: String, onClick: String => Future[_])
