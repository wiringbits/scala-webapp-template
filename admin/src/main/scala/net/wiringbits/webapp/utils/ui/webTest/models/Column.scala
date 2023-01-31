package net.wiringbits.webapp.utils.ui.webTest.models

import net.wiringbits.webapp.utils.ui.webTest.models.ColumnType
import net.wiringbits.webapp.utils.ui.webTest.models

case class Column(name: String, `type`: models.ColumnType, disabled: Boolean, filterable: Boolean)
