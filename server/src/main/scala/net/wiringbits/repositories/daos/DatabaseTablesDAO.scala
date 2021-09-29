package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.{Cell, ColumnMetadata, DatabaseTable, RowMetadata, TableMetadata}
import net.wiringbits.util.Pagination

import java.sql.{Connection, ResultSet}
import scala.collection.mutable.ListBuffer

object DatabaseTablesDAO {

  def all(schema: String = "public")(implicit conn: Connection): List[DatabaseTable] = {
    SQL"""
      SELECT table_name
      FROM information_schema.tables
      WHERE table_schema=$schema
        AND table_type='BASE TABLE'
      ORDER BY table_name
      """.as(tableParser.*)
  }

  def getTableMetadata(tableName: String, pagination: Pagination)(implicit conn: Connection): TableMetadata = {
    // I still haven't found how to use preparedStatement with tableName
    val sql = f"SELECT * FROM $tableName LIMIT ? OFFSET ?"
    val preparedStatement = conn.prepareStatement(sql)

    preparedStatement.setInt(1, pagination.limit)
    preparedStatement.setInt(2, pagination.offset)

    val resultSet = preparedStatement.executeQuery()

    try {
      val metadata = resultSet.getMetaData
      val numberOfColumns = metadata.getColumnCount

      val columnsMetadata = for {
        columnNumber <- 1 to numberOfColumns
        columnName = metadata.getColumnName(columnNumber)
        columnType = metadata.getColumnTypeName(columnNumber)
        columnMetadata = ColumnMetadata(columnName, columnType)
      } yield columnMetadata

      val tableData = getTableData(resultSet)
      TableMetadata(tableName, columnsMetadata.toList, tableData.toList)
    } finally {
      resultSet.close()
      preparedStatement.close()
    }

  }

  def getTableData(resultSet: ResultSet): ListBuffer[RowMetadata] = {
    val tableData = new ListBuffer[RowMetadata]()
    val metadata = resultSet.getMetaData
    val numberOfColumns = metadata.getColumnCount

    // It goes into the rows one by one
    while (resultSet.next) {
      val rowData = for {
        columnNumber <- 1 to numberOfColumns
        columnName = metadata.getColumnName(columnNumber)
        data = resultSet.getString(columnName)

        // This is just a workaround. I think it'll be better if I use a Option[T] syntax
        // so I'll do it later
        cell = if (data == null) Cell("null") else Cell(data)
      } yield cell

      tableData += RowMetadata(rowData.toList)
    }
    tableData
  }

}
