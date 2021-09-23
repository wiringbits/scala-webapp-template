package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.{ColumnMetadata, DatabaseTable, TableMetadata}

import java.sql.{Connection, ResultSet}

object DatabaseTablesDAO {

  def all()(implicit conn: Connection): List[DatabaseTable] = {
    // TODO: Not generic enough
    SQL"""
      SELECT table_name
      FROM information_schema.tables
      WHERE table_schema='public'
      AND table_type='BASE TABLE'
      ORDER BY table_name
      """.as(tableParser.*)
  }

  def getTableMetadata(tableName: String)(implicit conn: Connection): TableMetadata = {
    val tableData: Array[Array[String]] = Array()
    val columnsMetadata: Array[ColumnMetadata] = Array()

    val statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
    val resultSet = statement.executeQuery("SELECT * FROM " + tableName)
    val metadata = resultSet.getMetaData

    val numberOfColumns = metadata.getColumnCount

    while (resultSet.next) {
      val rowData: Array[String] = Array()
      for (columnNumber <- 1 to numberOfColumns) {
        val columnName = metadata.getColumnName(columnNumber)
        val columnType = metadata.getColumnTypeName(columnNumber)
        val field = resultSet.getString(columnName)

        val columnMetadata = ColumnMetadata(columnName, columnType)

        columnsMetadata :+ columnMetadata
        rowData :+ field

        println(resultSet.getObject(columnName))
        println(field)
      }
      tableData :+ rowData
    }

    val tableMetadata = TableMetadata(tableName, columnsMetadata, tableData)
    println(tableData.mkString("Array(", ", ", ")"))
    println(tableMetadata)

    tableMetadata
  }
}
