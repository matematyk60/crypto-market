package com.market.e2e.api.jwt

import java.security.PrivateKey
import java.time.Clock
import java.time.ZonedDateTime.now
import java.util.UUID

import com.market.e2e.domain.auth.TokenProvider
import pdi.jwt.{JwtClaim, JwtJson4s}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class JwtTokenProvider(
    privateKey: PrivateKey,
    registeredUserTokenDuration: FiniteDuration)(implicit clock: Clock)
    extends TokenProvider
    with JwtCommons {

  override def generateToken(id: Int): Future[String] =
    Future.successful(JwtJson4s.encode(marketClaim(id), privateKey, algorithm))

  private def marketClaim(id: Int) =
    JwtClaim(
      issuer = Some(marketIssuer),
      audience = Some(Set(marketAudience)),
      subject = Some(id.toString),
      expiration = Some(
        now(clock)
          .plusSeconds(registeredUserTokenDuration.toSeconds)
          .toEpochSecond),
      issuedAt = Some(now(clock).toEpochSecond),
      jwtId = Some(UUID.randomUUID().toString.filterNot(_ == '-').mkString),
    )
}
