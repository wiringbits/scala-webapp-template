package controllers

import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.AdminCreateTableRequest

import scala.util.control.NonFatal

class AdminControllerSpec extends PlayPostgresSpec {

  "GET /admin/tables/users" should {
    "return users table" in withApiClient { client =>
      val response = client.adminGetTableMetadata("users", 0, 10).futureValue
      response.name must be("users")
      response.fields mustNot be(empty)
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
    "fail when table doesn't exists" in withApiClient { client =>
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

  "POST /admin/tables/users" should {
    "create a new user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTableRequest(Map("name" -> name, "email" -> email, "password" -> password))

      val response = client.adminCreate("users", request).futureValue

      response.noData must be(empty)
    }

    "fail when an obligatory field is not sent" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTableRequest(Map("name" -> name))

      val error = client
        .adminCreate("users", request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"Requires: email, password")
    }
  }

  "fail when field in request doesn't exists" in withApiClient { client =>
    val name = "wiringbits"
    val nonExistentField = "nonExistentField"
    val request = AdminCreateTableRequest(Map("name" -> name, "nonExistentField" -> nonExistentField))

    val error = client
      .adminCreate("users", request)
      .map(_ => "Success when failure expected")
      .recover { case NonFatal(ex) =>
        ex.getMessage
      }
      .futureValue

    error must be(s"A field doesn't correspond to this table schema")
  }
}
