package net.wiringbits.components.pages

import net.wiringbits.components.widgets.{AppBar, Footer}
import net.wiringbits.ui.components.core.widgets._
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import typings.raCore.esmTypesMod.DataProvider

import scala.scalajs.js
import typings.reactAdmin.{components => reactAdmin}
import typings.raCore.esmTypesMod.DataProvider
import typings.raCore.{anon => raCore}

@react object HomePage {
  type Props = Unit

  // import { List, Datagrid, Edit, Create, SimpleForm, DateField, TextField, EditButton, TextInput, DateInput } from 'react-admin';
  //import BookIcon from '@material-ui/icons/Book';
  //export const PostIcon = BookIcon;
  //
  //export const PostList = (props) => (
  //    <List {...props}>
  //        <Datagrid>
  //            <TextField source="id" />
  //            <TextField source="title" />
  //            <DateField source="published_at" />
  //            <TextField source="average_note" />
  //            <TextField source="views" />
  //            <EditButton basePath="/posts" />
  //        </Datagrid>
  //    </List>
  //);
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val dataProvider = DataProvider(
      create = (_, _) => js.Promise.reject("Unimplemented"),
      delete = (_, _) => js.Promise.reject("Unimplemented"),
      deleteMany = (_, _) => js.Promise.reject("Unimplemented"),
      getList = (_, _) => js.Promise.reject("Unimplemented"),
      getMany = (_, _) => js.Promise.reject("Unimplemented"),
      getManyReference = (_, _) => js.Promise.reject("Unimplemented"),
      getOne = (_, _) => js.Promise.reject("Unimplemented"),
      update = (_, _) => js.Promise.reject("Unimplemented"),
      updateMany = (_, _) => js.Promise.reject("Unimplemented")
    )
    val admin = reactAdmin.Admin(dataProvider)(
      reactAdmin
        .Resource("users")
        .list(new ListGuesser)
    )
    admin
  }
}
// +    <Admin dataProvider={dataProvider}>
//+        <Resource name="users" list={ListGuesser} />
//+    </Admin>
