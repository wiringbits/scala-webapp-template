package net.wiringbits.repositories.models

case class TableMetadata(name: String, columns: Array[ColumnMetadata], rows: Array[Array[String]])
