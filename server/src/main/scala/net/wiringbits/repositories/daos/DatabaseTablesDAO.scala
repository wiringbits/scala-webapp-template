package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.config.models.DataExplorerSettings
import net.wiringbits.repositories.models.{Cell, DatabaseTable, TableField, TableMetadata, TableRow}
import net.wiringbits.util.models.pagination.{Count, Limit, Offset, PaginatedQuery, PaginatedResult}

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

  def getSettingsTables(tableSettings: DataExplorerSettings): List[DatabaseTable] = {
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

  def getTableFields(
      tableName: String
  )(implicit conn: Connection): IndexedSeq[TableField] = {
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
      } yield TableField(columnName, columnType)

    } finally {
      resultSet.close()
      preparedStatement.close()
    }

  }

  def getTableData(
      tableName: String,
      fields: IndexedSeq[TableField],
      pagination: PaginatedQuery,
      tableSettings: DataExplorerSettings
  )(implicit conn: Connection): PaginatedResult[TableMetadata] = {
    val tableData = new ListBuffer[TableRow]()

    val indexOfItem = tableSettings.tables.indexWhere(_.name == tableName)
    val orderBy = tableSettings.tables(indexOfItem).defaultOrderByClause
    val count = countRecordsOnTable(tableName)

    val SQL =
      s"""
        SELECT * FROM $tableName
        ORDER BY ?
        LIMIT ? OFFSET ?
        """

    val preparedStatement = conn.prepareStatement(SQL)
    preparedStatement.setString(1, orderBy.string)
    preparedStatement.setInt(2, pagination.limit.int)
    preparedStatement.setInt(3, pagination.offset.int)

    val resultSet = preparedStatement.executeQuery()

    // It goes into the rows one by one
    while (resultSet.next) {
      val rowData = for {
        field <- fields
        fieldName = field.name
        data = resultSet.getString(fieldName)

        // This is just a workaround. I think it'll be better if I use a Option[T] syntax
        // so I'll do it later
        cell = if (data == null) Cell("null") else Cell(data)
      } yield cell

      tableData += TableRow(rowData.toList)
    }
    val table = TableMetadata(tableName, fields.toList, tableData.toList)

    PaginatedResult[TableMetadata](table, Offset(pagination.offset.int), Limit(pagination.limit.int), Count(count))
  }

  def countRecordsOnTable(tableName: String)(implicit conn: Connection): Int = {
    val SQL = s"SELECT COUNT(*) FROM $tableName"

    val preparedStatement = conn.prepareStatement(SQL)
    val resultSet = preparedStatement.executeQuery()
    try {
      resultSet.next()
      resultSet.getInt(1)
    } finally {
      preparedStatement.close()
      resultSet.close()
    }
  }
}
