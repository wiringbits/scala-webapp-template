package net.wiringbits.repositories.models

case class TableMetadata(name: String, columns: List[ColumnMetadata], rows: List[RowMetadata])
