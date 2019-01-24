package com.market.e2e.domain.offer

import com.market.e2e.domain.currency.{Currency, UsdRateProvider}

import scala.concurrent.{ExecutionContext, Future}

class OfferMatcher(usdRateProvider: UsdRateProvider)(
    implicit ec: ExecutionContext) {

  def areMatching(firstOffer: ExchangeOffer,
                  secondOffer: ExchangeOffer): Future[Boolean] = {
    def inPlaceMatching = (firstOffer, secondOffer) match {
      case (ExchangeOffer(_,
                          _,
                          _,
                          fSellingCurrency,
                          fBuyingCurrency,
                          fAmount,
                          fMinimalRateSellingToBuying,
                          fMinimumSelled,
                          _),
            ExchangeOffer(_,
                          _,
                          _,
                          sSellingCurrency,
                          sBuyingCurrency,
                          sAmount,
                          sMinimalRateSellingToBuying,
                          sMinimumSelled,
                          _)) =>
        lazy val areCurrenciesMatching = fSellingCurrency == sBuyingCurrency && fBuyingCurrency == sSellingCurrency
        lazy val areRatesMatching = fMinimalRateSellingToBuying <= (1 / sMinimalRateSellingToBuying)
        lazy val fExchangeRatio = (fMinimalRateSellingToBuying + (1 / sMinimalRateSellingToBuying)) / 2
        lazy val sEchangeRatio = 1 / fExchangeRatio
        lazy val fSelled = math.min(sEchangeRatio * sAmount, fAmount)
        lazy val sSelled = math.min(fExchangeRatio * fAmount, sAmount)
        lazy val areMinimumAmountsMatching = fSelled >= fMinimumSelled && sSelled >= sMinimumSelled
        areCurrenciesMatching && areRatesMatching && areMinimumAmountsMatching
    }

    if (!inPlaceMatching) Future.successful(false)
    else {
      println(s"PIERWSZA $firstOffer DRUGA $secondOffer")
      (firstOffer.exchangeIf, secondOffer.exchangeIf) match {
        case (Some(CurrencyRateOver(currency, minimumRate)), None) =>
          currencyUsdRateOver(currency, minimumRate)
        case (None, Some(CurrencyRateOver(currency, minimumRate))) =>
          currencyUsdRateOver(currency, minimumRate)
        case (Some(CurrencyRateOver(fCurrency, fMinimumRate)),
              Some(CurrencyRateOver(sCurrency, sMinimumRate))) =>
          for {
            firstGood <- currencyUsdRateOver(fCurrency, fMinimumRate)
            secondGood <- currencyUsdRateOver(sCurrency, sMinimumRate)
          } yield firstGood && secondGood
        case _ => Future.successful(true)
      }
    }
  }

  private def currencyUsdRateOver(currency: Currency, minimalRate: Double) =
    usdRateProvider
      .currencyUsdRate(currency)
      .map(usdRate => usdRate >= minimalRate)

}
