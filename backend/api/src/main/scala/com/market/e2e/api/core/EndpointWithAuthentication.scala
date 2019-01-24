package com.market.e2e.api.core

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.{
  CredentialsMissing,
  CredentialsRejected
}
import akka.http.scaladsl.server.{
  AuthenticationFailedRejection,
  Directive1,
  MalformedHeaderRejection
}
import com.market.e2e.api.jwt.JwtAuthenticator
import com.market.e2e.domain.TypeAliases.UserId

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

abstract class EndpointWithAuthentication(authenticator: JwtAuthenticator)(
    implicit ec: ExecutionContext)
    extends Endpoint {

  private val rejectDueToCredentialsRejected: Directive1[UserId] = reject(
    AuthenticationFailedRejection(CredentialsRejected,
                                  HttpChallenge("Bearer", None)))

  protected def authenticate: Directive1[UserId] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(header) =>
        header.split(" ").toList match {
          case "Bearer" :: payload :: Nil =>
            validateToken(payload)
          case _ =>
            reject(
              MalformedHeaderRejection("Authorization", "Illegal header key."))
        }
      case None =>
        reject(
          AuthenticationFailedRejection(CredentialsMissing,
                                        HttpChallenge("Bearer", None)))
    }

  def validateToken(token: String): Directive1[UserId] = {
    authenticator.validate(token) match {
      case Success(result) => provide(result)
      case Failure(exception) =>
        logger.warn(s"Failed to parse token ", exception)
        rejectDueToCredentialsRejected
    }
  }
}
