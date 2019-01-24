package com.market.unit.mock

import com.market.e2e.domain.currency.Currencys.{BitCoin, Litecoin}
import com.market.e2e.domain.currency.{Currency, UsdRateProvider}
import com.market.e2e.domain.offer.OfferMatcher

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object OfferMatcherMocks {

  val noExchangeIfMatcher = new OfferMatcher(usdRateProvider = null)

  def exchangeIfMatcher(bitcoinUsdRateOver: Double) =
    new OfferMatcher(new UsdRateProvider {
      override def currencyUsdRate(currency: Currency): Future[Double] =
        currency match {
          case BitCoin  => Future.successful(bitcoinUsdRateOver)
          case Litecoin => ???
        }
    })

}
