package controllers

import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.CreateUserRequest

import scala.util.control.NonFatal

class UsersControllerSpec extends PlayPostgresSpec {

  "POST /users" should {
    "return the authentication token after creating a user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = CreateUserRequest(name = name, email = email, password = "test123...")

      val response = client.createUser(request).futureValue
      response.name must be(name)
      response.email must be(email)
      response.token mustNot be(empty)
    }

    "fail when the email is already taken" in withApiClient { client =>
      val request = CreateUserRequest(name = "someone", email = "test@wiringbits.net", password = "test123...")

      // take the email
      client.createUser(request).futureValue

      // then, it fails
      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("Email already in use, pick another one")
    }

    "fail when the email has a wrong format" in withApiClient { client =>
      val request = CreateUserRequest(name = "someone", email = "test1@email.@", password = "test123...")

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("Invalid email address")
    }

    "fail when the password is too short" in withApiClient { client =>
      val request = CreateUserRequest(name = "someone", email = "test1@email.com", password = "test123")

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("The password must contain at least 8 characters")
    }

    "fail when the name is too short" in withApiClient { client =>
      val request = CreateUserRequest(name = "n", email = "test2@email.com", password = "test123...")

      val error = client
        .createUser(request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be("The name must contain at least 2 characters")
    }
  }
}
