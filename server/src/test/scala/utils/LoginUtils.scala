package utils

import net.wiringbits.api.{ApiClient, SttpTapirApiClient}
import net.wiringbits.api.models.{CreateUser, Login, VerifyEmail}
import net.wiringbits.common.models.{Captcha, Password, UserToken}
import net.wiringbits.util.TokenGenerator
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.*
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import org.mockito.Mockito.*

trait LoginUtils {

  def createVerifyLoginUser(
      request: CreateUser.Request,
      client: SttpTapirApiClient,
      tokenGenerator: TokenGenerator
  )(implicit ec: ExecutionContext): Future[Login.Response] = {

    val verificationToken = UUID.randomUUID()

    when(tokenGenerator.next()).thenReturn(verificationToken)

    for {
      user <- client.createUser(request)
      _ <- client.verifyEmail(VerifyEmail.Request(UserToken(user.id, verificationToken)))

      loginRequest = Login.Request(
        email = user.email,
        password = Password.trusted("test123..."),
        captcha = Captcha.trusted("test")
      )
      response <- client.login(loginRequest)
    } yield response
  }

}
