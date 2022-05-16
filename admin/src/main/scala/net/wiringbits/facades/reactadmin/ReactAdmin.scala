package net.wiringbits.facades.reactadmin

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import japgolly.scalajs.react.facade.React.ElementType

import scala.annotation.unused

@js.native
@JSImport("react-admin", JSImport.Namespace)
object ReactAdmin extends js.Any {
  def useRecordContext(): js.Dictionary[js.Any] = js.native

  val EditGuesser, ListGuesser: ElementType = js.native

  val Admin, Create, Datagrid, Edit, EditButton, EmailField, List, ReferenceField, ReferenceInput, Resource,
      SelectInput, SimpleForm, TextField, TextInput, UrlField, fetchUtils: js.Object =
    js.native
}
