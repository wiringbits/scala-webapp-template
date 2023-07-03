package utils

import net.wiringbits.api.ApiClient
import net.wiringbits.api.models.{CreateUser, Login, VerifyEmail}
import net.wiringbits.common.models.{Captcha, Password, UserToken}
import net.wiringbits.util.TokenGenerator
//import org.mockito.MockitoSugar.when
import eu.monniot.scala3mock.matchers.MatchAny
import eu.monniot.scala3mock.macros.{mock, when}
import eu.monniot.scala3mock.main.withExpectations
import eu.monniot.scala3mock.main.Default

import org.scalatestplus.mockito.MockitoSugar._

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait LoginUtils{


  def createVerifyLoginUser(
      request: CreateUser.Request,
      client: ApiClient,
      tokenGenerator: TokenGenerator
  )(implicit ec: ExecutionContext): Future[Login.Response] = {


    val verificationToken = UUID.randomUUID()
    when(()=>tokenGenerator.next()).expects().returns(verificationToken)



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
