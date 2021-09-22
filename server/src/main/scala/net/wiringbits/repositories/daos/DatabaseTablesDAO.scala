package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.DatabaseTable

import java.sql.Connection

object DatabaseTablesDAO {

  def all()(implicit conn: Connection): List[DatabaseTable] = {
    // TODO: Not generic enough
    SQL"""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema='public'
        AND table_type='BASE TABLE';
       """.as(tableParser.*)
  }
}
