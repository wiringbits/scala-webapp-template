package net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-admin", JSImport.Namespace)
object ReactAdmin extends js.Any {
  def useRecordContext(): js.Dictionary[js.Any] = js.native

  def useEditContext(): js.Dictionary[js.Any] = js.native

  val Admin, Create, Datagrid, Edit, EditButton, EmailField, List, ListBase, ReferenceField, ReferenceInput, Resource,
      SelectInput, SimpleForm, TextField, DateField, DateTimeInput, TextInput, Button, SaveButton, DeleteButton,
      SearchInput, FilterButton, FilterForm, Toolbar, TopToolbar, ExportButton, CreateButton, UrlField: js.Object =
    js.native
}
