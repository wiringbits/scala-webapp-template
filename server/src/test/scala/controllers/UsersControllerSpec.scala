package controllers

import controllers.common.PlayPostgresSpec
import net.wiringbits.api.models.{CreateUserRequest, CreateUserResponse, ErrorResponse}
import play.api.libs.json.Json
import play.api.test.Helpers._

class UsersControllerSpec extends PlayPostgresSpec {

  "POST /users" should {
    "return the authentication token after creating a user" in withApplication { implicit app =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val body = CreateUserRequest(name = name, email = email, password = "test123...")

      val response = POST("/users", Json.toJson(body).toString())
      status(response) must be(OK)

      val result = contentAsJson(response).as[CreateUserResponse]
      result.name must be(name)
      result.email must be(email)
      result.token mustNot be(empty)
    }

    "fail when the email is already taken" in withApplication { implicit app =>
      val body = CreateUserRequest(name = "someone", email = "test@wiringbits.net", password = "test123...")

      status(POST("/users", Json.toJson(body).toString())) must be(OK)

      val response = POST("/users", Json.toJson(body).toString())
      status(response) must be(INTERNAL_SERVER_ERROR)

      val result = contentAsJson(response).as[ErrorResponse]
      result.error must be("Email already in use, pick another one")
    }

    "fail when the email has a wrong format" in withApplication { implicit app =>
      val body = CreateUserRequest(name = "someone", email = "test1@email.@", password = "test123...")

      val response = POST("/users", Json.toJson(body).toString())
      status(response) must be(INTERNAL_SERVER_ERROR)

      val result = contentAsJson(response).as[ErrorResponse]
      result.error must be("Invalid email address")
    }

    "fail when the password is too short" in withApplication { implicit app =>
      val body = CreateUserRequest(name = "someone", email = "test1@email.com", password = "test123")

      val response = POST("/users", Json.toJson(body).toString())
      status(response) must be(INTERNAL_SERVER_ERROR)

      val result = contentAsJson(response).as[ErrorResponse]
      result.error must be("The password must contain at least 8 characters")
    }

    "fail when the name is too short" in withApplication { implicit app =>
      val body = CreateUserRequest(name = "n", email = "test2@email.com", password = "test123...")

      val response = POST("/users", Json.toJson(body).toString())
      status(response) must be(INTERNAL_SERVER_ERROR)

      val result = contentAsJson(response).as[ErrorResponse]
      result.error must be("The name must contain at least 2 characters")
    }
  }
}
