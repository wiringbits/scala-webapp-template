package net.wiringbits.repositories.daos

import anorm.SqlStringInterpolation
import net.wiringbits.repositories.models.Table

import java.sql.Connection

object TablesDAO {

  def all()(implicit conn: Connection): List[Table] = {
    SQL"""
        SELECT table_name
          FROM information_schema.tables
        WHERE table_schema='public'
          AND table_type='BASE TABLE';
       """.as(tableParser.*)
  }

}
