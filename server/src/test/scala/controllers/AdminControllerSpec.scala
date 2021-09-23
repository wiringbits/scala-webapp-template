package controllers

import controllers.common.PlayPostgresSpec

class AdminControllerSpec extends PlayPostgresSpec {

  "GET /admin/tables/users" should {
    "return users database" in withApiClient { client =>
      val response = client.adminGetFields("users").futureValue
      println(response)
      response.data mustNot be(empty)
    }
  }

}
