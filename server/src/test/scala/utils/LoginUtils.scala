package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.auth.Login
import net.wiringbits.api.models.users.{CreateUser, VerifyEmail}
import net.wiringbits.common.models.*
import net.wiringbits.util.TokenGenerator
import org.mockito.Mockito.*

import java.util.UUID
import scala.annotation.unused
import scala.concurrent.{ExecutionContext, Future}

trait LoginUtils {
  def createUser(
      nameMaybe: Option[Name] = None,
      emailMaybe: Option[Email] = None,
      passwordMaybe: Option[Password] = None
  )(using
      @unused ec: ExecutionContext,
      client: ApiClient
  ): Future[CreateUser.Response] = {
    val request = CreateUser.Request(
      name = nameMaybe.getOrElse(Name.trusted("wiringbits")),
      email = emailMaybe.getOrElse(Email.trusted(s"test${UUID.randomUUID()}@email.com")),
      password = passwordMaybe.getOrElse(Password.trusted("test123...")),
      captcha = Captcha.trusted("test")
    )

    client.createUser(request)
  }

  def createVerifyLoginUser(
      tokenGenerator: TokenGenerator,
      nameMaybe: Option[Name] = None,
      emailMaybe: Option[Email] = None,
      passwordMaybe: Option[Password] = None
  )(using @unused ec: ExecutionContext, client: ApiClient): Future[Login.Response] = {
    val verificationToken = UUID.randomUUID()
    when(tokenGenerator.next()).thenReturn(verificationToken)

    for {
      user <- createUser(nameMaybe, emailMaybe, passwordMaybe)
      _ <- client.verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken)))
      loginRequest = Login.Request(
        email = user.email,
        password = passwordMaybe.getOrElse(Password.trusted("test123...")),
        captcha = Captcha.trusted("test")
      )
      response <- client.login(loginRequest)
    } yield response
  }
}
