package net.wiringbits.repositories.models

case class TableMetadata(name: String, fields: List[TableField], rows: List[TableRow])
