package anorm

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
case class MetaDataItem(column: ColumnName, nullable: Boolean, clazz: String)
