package api

import net.wiringbits.webapp.utils.admin.controllers.AdminController
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.Inject

class AdminDataExplorerRouter @Inject() (adminController: AdminController) extends SimpleRouter {

  override def routes: Routes = {
    // get database tables
    case GET(p"/admin/tables") =>
      adminController.getTables()

    // get database table fields
    case GET(p"/admin/tables/$tableName" ? q_o"offset=${int(offsetOpt)}" & q_o"limit=${int(limitOpt)}") =>
      val offset = offsetOpt.getOrElse(0)
      val limit = limitOpt.getOrElse(10)
      adminController.getTableMetadata(tableName, offset, limit)

    // get table resource by id (depends on IDFieldName on AdminConfig)
    case GET(p"/admin/tables/$tableName/$id") =>
      adminController.find(tableName, id)

    // create table resource
    case POST(p"/admin/tables/$tableName") =>
      adminController.create(tableName)

    // update table resource
    case PUT(p"/admin/tables/$tableName/$id") =>
      adminController.update(tableName, id)

    // delete table resource
    case DELETE(p"/admin/tables/$tableName/$id") =>
      adminController.delete(tableName, id)
  }
}
