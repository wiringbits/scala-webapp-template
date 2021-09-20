package net.wiringbits.repositories.models

case class Table(
    table_name: String
)

object Table {
  case class CreateTable(table_name: String)
}
