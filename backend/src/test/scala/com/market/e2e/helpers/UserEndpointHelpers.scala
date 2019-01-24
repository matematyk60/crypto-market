package com.market.e2e.helpers

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.server.Route
import com.market.e2e.specs.{EndpointTestSpec, TokenSpec}

trait UserEndpointHelpers extends EndpointTestSpec with TokenSpec {

  def getMyFunds(token: String)(checkResults: => Any)(
      implicit endpoint: Route): Any =
    TokenRequest("/user/me/funds", token) ~> endpoint ~> check(checkResults)

  def getMyFundsInCurrency(token: String, currency: String)(
      checkResults: => Any)(implicit endpoint: Route): Any =
    TokenRequest(s"/user/me/funds/currency/$currency", token) ~> endpoint ~> check(
      checkResults)

  def addFunds(token: String, currency: String, amount: Double)(
      checkResults: => Any)(implicit endpoint: Route): Any =
    TokenRequest("/user/me/funds",
                 token,
                 Some(Map("currency" -> currency, "amount" -> amount)),
                 method = HttpMethods.POST) ~> endpoint ~> check(checkResults)

}
