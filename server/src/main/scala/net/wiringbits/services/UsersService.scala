package net.wiringbits.services

import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.common.models.Captcha
import net.wiringbits.api.models.{CreateUser, GetCurrentUser, Login, UpdateUser}
import net.wiringbits.config.JwtConfig
import net.wiringbits.repositories
import net.wiringbits.repositories.models.User
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}
import net.wiringbits.util.JwtUtils
import org.apache.commons.validator.routines.EmailValidator
import org.mindrot.jbcrypt.BCrypt

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UsersService @Inject() (
    jwtConfig: JwtConfig,
    repository: UsersRepository,
    userLogsRepository: UserLogsRepository,
    captchaApi: ReCaptchaApi
)(implicit
    ec: ExecutionContext
) {
  private implicit val clock: Clock = Clock.systemUTC

  // returns the login token
  def create(request: CreateUser.Request): Future[CreateUser.Response] = {
    val validations = {
      for {
        _ <- validateCaptcha(request.captcha)
        _ <- Future {
          validateName(request.name)
          validatePassword(request.password)
        }
        _ <- validateEmail(request.email)
      } yield ()
    }

    for {
      _ <- validations
      hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
      createUser = repositories.models.User
        .CreateUser(id = UUID.randomUUID(), name = request.name, email = request.email, hashedPassword = hashedPassword)
      _ <- repository.create(createUser)
      _ <- userLogsRepository.create(
        createUser.id,
        s"Account created, name = ${request.name}, email = ${request.email}"
      )
      token = JwtUtils.createToken(jwtConfig, createUser.id)
    } yield CreateUser.Response(request.name, request.email, token)
  }

  // returns the token to use for authenticating requests
  def login(request: Login.Request): Future[Login.Response] = {
    for {
      _ <- validateCaptcha(request.captcha)
      maybe <- repository.find(request.email)
      user = maybe
        .filter(user => BCrypt.checkpw(request.password, user.hashedPassword))
        .getOrElse(throw new RuntimeException("The given email/password doesn't match"))
      _ <- userLogsRepository.create(user.id, "Logged in successfully")
      token = JwtUtils.createToken(jwtConfig, user.id)
    } yield Login.Response(user.name, user.email, token)
  }

  def update(userId: UUID, request: UpdateUser.Request): Future[Unit] = {
    val validate = Future {
      if (request.name.isEmpty) new RuntimeException(s"the name is required")
      else ()
    }

    for {
      _ <- validate
      user <- unsafeUser(userId)
      _ <- repository.update(userId, request.name)
      _ <- userLogsRepository.create(userId, s"Name changed from ${user.name} to ${request.name}")
    } yield ()
  }

  def getCurrentUser(userId: UUID): Future[GetCurrentUser.Response] = {
    for {
      user <- unsafeUser(userId)
    } yield GetCurrentUser.Response(
      id = user.id,
      email = user.email,
      name = user.name
    )
  }

  private def unsafeUser(userId: UUID): Future[User] = {
    repository
      .find(userId)
      .map { maybe =>
        maybe.getOrElse(
          throw new RuntimeException(
            s"Unexpected error because the user wasn't found: $userId"
          )
        )
      }
  }

  private def validateEmail(email: String): Future[Unit] = {
    val formatValidation = Future {
      val validator = EmailValidator.getInstance()
      if (!validator.isValid(email)) {
        throw new RuntimeException(s"Invalid email address")
      } else ()
    }

    for {
      _ <- formatValidation
      maybe <- repository.find(email)
    } yield {
      if (maybe.isDefined) throw new RuntimeException(s"Email already in use, pick another one")
      else ()
    }
  }

  private def validateCaptcha(captcha: Captcha): Future[Unit] = {
    captchaApi
      .verify(captcha)
      .map(valid => if (!valid) throw new RuntimeException(s"Invalid captcha, try again") else ())
  }

  private def validateName(name: String): Unit = {
    val minLength = 2
    if (name.length < minLength) {
      throw new RuntimeException(s"The name must contain at least $minLength characters")
    } else ()
  }

  private def validatePassword(pass: String): Unit = {
    val minLength = 8
    if (pass.length < minLength) {
      throw new RuntimeException(s"The password must contain at least $minLength characters")
    } else ()
  }
}
