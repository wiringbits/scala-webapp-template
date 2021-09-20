package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.TablesDAO
import net.wiringbits.repositories.models.Table
import play.api.db.Database

import javax.inject.Inject
import scala.concurrent.Future

class TablesRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext) {

  def all(): Future[List[Table]] = Future {
    database.withConnection { implicit conn =>
      TablesDAO.all()
    }
  }

}
