package controllers

import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.CreateUser
import net.wiringbits.common.models.{Email, Name, Password}

import scala.util.control.NonFatal

class UsersControllerSpec extends PlayPostgresSpec {

  "POST /users" should {
    "return the authentication token after creating a user" in withApiClient { client =>
      val name = Name.trusted("wiringbits")
      val email = Email.trusted("test@wiringbits.net")
      val request = CreateUser.Request(name = name, email = email, password = Password.trusted("test123..."))

      val response = client.createUser(request).futureValue
      response.name must be(name)
      response.email must be(email)
      response.token mustNot be(empty)
    }

    "fail when the email is already taken" in withApiClient { client =>
      val request = CreateUser.Request(
        name = Name.trusted("someone"),
        email = Email.trusted("test@wiringbits.net"),
        password = Password.trusted("test123...")
      )

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
  }
}
