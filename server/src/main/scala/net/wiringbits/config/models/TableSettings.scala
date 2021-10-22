package net.wiringbits.config.models

import net.wiringbits.util.models.ordering.OrderingCondition

case class TableSettings(tableName: String, defaultOrderByClause: OrderingCondition, IDFieldName: String)
