package net.wiringbits.util

import net.wiringbits.config.JwtConfig
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.Json

import java.time.Clock
import java.util.UUID
import scala.concurrent.duration._
import scala.util.Try

object JwtUtils {

  def createToken(config: JwtConfig, userId: UUID)(implicit clock: Clock): String = {
    val json = s"""{ "id": "$userId" }"""
    val claim = JwtClaim(json).issuedNow
      .expiresIn(30.days.toSeconds)

    val token = Jwt.encode(claim, config.secret, JwtAlgorithm.HS384)
    token
  }

  def decodeToken(config: JwtConfig, token: String)(implicit clock: Clock): Try[UUID] = {
    Jwt
      .decode(token, config.secret, Seq(JwtAlgorithm.HS384))
      .filter(_.isValid)
      .map(_.content)
      .map { decodedClaim =>
        val json = Json.parse(decodedClaim)
        val id = (json \ "id").as[UUID]
        id
      }
  }
}
