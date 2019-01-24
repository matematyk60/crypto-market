package com.market.e2e.helpers

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.server.Route
import com.market.e2e.specs.{EndpointTestSpec, TokenSpec}

trait OfferEndpointHelpers extends EndpointTestSpec with TokenSpec {

  protected def addNewOffer(token: String,
                            selling: String,
                            buying: String,
                            amount: Double,
                            minimalRateSellingToBuying: Double,
                            minimumSelled: Double,
                            currencyRateOverCurrency: Option[String] = None,
                            currencyRateOverRate: Option[Double] = None)(
      checkResults: => Any)(implicit endpoint: Route): Any = {

    val optionalEntity = currencyRateOverCurrency
      .map(
        currency =>
          Map("exchangeIf" -> Map("currency" -> currency,
                                  "minimumRate" -> currencyRateOverRate)))
      .getOrElse(Map())

    val fullEntity = optionalEntity ++ Map(
      "selling" -> selling,
      "buying" -> buying,
      "amount" -> amount,
      "minimalRateSellingToBuying" -> minimalRateSellingToBuying,
      "minimumSelled" -> minimumSelled)

    TokenRequest("/offer",
                 token,
                 content = Some(fullEntity),
                 method = HttpMethods.POST) ~> endpoint ~> check(checkResults)
  }

  protected def exchangeWithOffer(token: String, offerId: String)(
      checkResults: => Any)(implicit endpoint: Route): Any =
    TokenRequest(s"/offer/$offerId/exchange", token, method = HttpMethods.POST) ~> endpoint ~> check(
      checkResults)

  protected def getAllOffers(token: String)(checkResults: => Any)(
      implicit endpoint: Route): Any =
    TokenRequest(s"/offers", token) ~> endpoint ~> check(checkResults)

  protected def getSingleOffer(token: String, offerId: String)(
      checkResults: => Any)(implicit endpoint: Route): Any =
    TokenRequest(s"/offer/$offerId", token) ~> endpoint ~> check(checkResults)
}
