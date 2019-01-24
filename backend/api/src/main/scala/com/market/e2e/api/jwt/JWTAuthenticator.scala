package com.market.e2e.api.jwt

import java.security.PublicKey

import com.market.e2e.domain.TypeAliases.UserId
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import pdi.jwt.JwtJson4s

import scala.util.Try

class JwtAuthenticator(publicKey: PublicKey)
    extends StrictLogging
    with Json4sSupport
    with JwtCommons {

  def validate(token: String): Try[UserId] =
    JwtJson4s.decode(token, publicKey, Seq(algorithm)).map { claim =>
      if (!claim.issuer.forall(_ == marketIssuer))
        throw InvalidTokenPayload(s"Invalid issuer [${claim.issuer}]")
      if (!claim.audience.forall(_.contains(marketAudience)))
        throw InvalidTokenPayload(s"Invalid audience [${claim.audience}]")
      (for {
        id <- claim.subject.toRight(s"Invalid subject [${claim.audience}]")
      } yield id.toInt) match {
        case Right(authenticatedUser) => authenticatedUser
        case Left(error)              => throw InvalidTokenPayload(error)
      }
    }
}

final case class InvalidTokenPayload(message: String) extends Throwable
