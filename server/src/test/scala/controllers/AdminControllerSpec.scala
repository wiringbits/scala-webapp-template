package controllers

import controllers.common.PlayPostgresSpec

import scala.util.control.NonFatal

class AdminControllerSpec extends PlayPostgresSpec {

  "GET /admin/tables/users" should {
    "return users database" in withApiClient { client =>
      val response = client.adminGetTableMetadata("users", 0, 10).futureValue
      response.name must be("users")
      response.columns mustNot be(empty)
    }

    "fail when you enter negative offset" in withApiClient { client =>
      val error = client
        .adminGetTableMetadata("users", -1, 10)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"You can't query a table using negative numbers as a limit or offset")
    }

    "fail when you enter negative limit" in withApiClient { client =>
      val error = client
        .adminGetTableMetadata("users", 0, -1)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"You can't query a table using negative numbers as a limit or offset")
    }
  }

  "GET /admin/tables/aaaaaaaaaaa" should {
    "fail when database doesn't exists" in withApiClient { client =>
      val invalidTableName = "aaaaaaaaaaa"
      val error = client
        .adminGetTableMetadata(invalidTableName, 0, 10)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be(s"Unexpected error because the DB table wasn't found: $invalidTableName")
    }
  }
}
