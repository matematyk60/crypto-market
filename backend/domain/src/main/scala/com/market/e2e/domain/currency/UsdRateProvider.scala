package com.market.e2e.domain.currency

import scala.concurrent.Future

trait UsdRateProvider {
  def currencyUsdRate(currency: Currency): Future[Double]
}
