package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.config.AdminConfig
import net.wiringbits.repositories.models.{Cell, ColumnMetadata, DatabaseTable, RowMetadata, TableMetadata}
import net.wiringbits.util.Pagination

import java.sql.Connection
import scala.collection.mutable.ListBuffer

object DatabaseTablesDAO {

  def all()(implicit conn: Connection): List[DatabaseTable] = {
    SQL"""
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'public'
      AND table_type = 'BASE TABLE'
    ORDER BY table_name
    """.as(tableParser.*)
  }

  def getSettingsTables(tableSettings: AdminConfig): List[DatabaseTable] = {
    for {
      table <- tableSettings.tables
      tableName = table.name
    } yield DatabaseTable(tableName)
  }

  def allSQL(schema: String = "public")(implicit conn: Connection): List[DatabaseTable] = {
    SQL"""
        SELECT table_name 
        FROM information_schema.tables
        WHERE table_schema = $schema
          AND table_type = 'BASE TABLE'
        """.as(tableParser.*)

  }

  def getTableMetadata(
      tableName: String
  )(implicit conn: Connection): IndexedSeq[ColumnMetadata] = {
    val sql = f"SELECT * FROM $tableName LIMIT 0"

    val preparedStatement = conn.prepareStatement(sql)

    val resultSet = preparedStatement.executeQuery()

    try {
      val metadata = resultSet.getMetaData
      val numberOfColumns = metadata.getColumnCount

      for {
        columnNumber <- 1 to numberOfColumns
        columnName = metadata.getColumnName(columnNumber)
        columnType = metadata.getColumnTypeName(columnNumber)
      } yield ColumnMetadata(columnName, columnType)

    } finally {
      resultSet.close()
      preparedStatement.close()
    }

  }

  def getTableData(
      tableName: String,
      columns: IndexedSeq[ColumnMetadata],
      pagination: Pagination,
      tableSettings: AdminConfig
  )(implicit conn: Connection): TableMetadata = {
    val tableData = new ListBuffer[RowMetadata]()

    val indexOfItem = tableSettings.tables.indexWhere(_.name == tableName)
    val orderBy = tableSettings.tables(indexOfItem).defaultOrderByClause

    val SQL =
      s"""
        SELECT * FROM $tableName
        ORDER BY ?
        LIMIT ? OFFSET ?
        """

    val preparedStatement = conn.prepareStatement(SQL)
    preparedStatement.setString(1, orderBy)
    preparedStatement.setInt(2, pagination.limit)
    preparedStatement.setInt(3, pagination.offset)

    val resultSet = preparedStatement.executeQuery()

    // It goes into the rows one by one
    while (resultSet.next) {
      val rowData = for {
        column <- columns
        columnName = column.name
        data = resultSet.getString(columnName)

        // This is just a workaround. I think it'll be better if I use a Option[T] syntax
        // so I'll do it later
        cell = if (data == null) Cell("null") else Cell(data)
      } yield cell

      tableData += RowMetadata(rowData.toList)
    }
    TableMetadata(tableName, columns.toList, tableData.toList)
  }

}
