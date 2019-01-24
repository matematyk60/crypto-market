package com.market.e2e.specs

import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, GenericHttpCredentials}

import scala.collection.immutable
import scala.language.postfixOps

trait TokenSpec { restSpec: EndpointTestSpec =>

  protected def TokenRequest(uri: String,
                             token: String,
                             content: Option[AnyRef] = None,
                             method: HttpMethod = GET) =
    HttpRequest(
      method,
      uri = uri,
      headers = immutable.Seq[HttpHeader](
        Authorization(GenericHttpCredentials("Bearer", token))),
      content.toJsonEntity
    )
}
