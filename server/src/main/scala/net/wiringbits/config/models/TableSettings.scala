package net.wiringbits.config.models

import net.wiringbits.util.models.ordering.OrderingCondition

case class TableSettings(name: String, defaultOrderByClause: OrderingCondition)
