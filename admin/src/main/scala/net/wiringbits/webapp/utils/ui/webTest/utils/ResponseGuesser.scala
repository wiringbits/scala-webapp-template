package net.wiringbits.webapp.utils.ui.webTest.utils

import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.webTest.models.{Column, ColumnType}

object ResponseGuesser {
  def getTypesFromResponse(response: AdminGetTables.Response.DatabaseTable): List[Column] = {
    response.columns.map { column =>
      val fieldType = ColumnType.fromTableField(column)
      Column(name = column.name, `type` = fieldType, disabled = !column.editable, filterable = column.filterable)
    }
  }
}
