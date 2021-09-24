package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.{Cell, ColumnMetadata, DatabaseTable, RowMetadata, TableMetadata}

import java.sql.{Connection, ResultSet}
import scala.collection.mutable.ListBuffer

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
    val tableData = new ListBuffer[RowMetadata]()
    val columnsMetadata = new ListBuffer[ColumnMetadata]()

    val statement = conn.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_READ_ONLY)
    val resultSet = statement.executeQuery("SELECT * FROM " + tableName)
    val metadata = resultSet.getMetaData

    val numberOfColumns = metadata.getColumnCount

    for (columnNumber <- 1 to numberOfColumns) {
      val columnName = metadata.getColumnName(columnNumber)
      val columnType = metadata.getColumnTypeName(columnNumber)
      val columnMetadata = ColumnMetadata(columnName, columnType)
      columnsMetadata += columnMetadata
    }

    // It goes into the rows one by one
    while (resultSet.next) {
      val rowData = new ListBuffer[Cell]
      for (columnNumber <- 1 to numberOfColumns) {
        val columnName = metadata.getColumnName(columnNumber)

        val data = resultSet.getString(columnName)

        // This is just a workaround. I think it'll be better if I use a Option[T] syntax
        // so I'll do it later
        val cell = if (data == null) Cell("null") else Cell(data)
        rowData += cell
      }
      tableData += RowMetadata(rowData.toList)
    }

    statement.close()
    resultSet.close()
    val tableMetadata = TableMetadata(tableName, columnsMetadata.toList, tableData.toList)

    tableMetadata
  }
}
