package com.market.e2e.domain.offer

import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.currency.{Currency, UsdRateProvider}

import scala.concurrent.{ExecutionContext, Future}

final case class ExchangeOffer(
    offerId: String,
    userId: UserId,
    finished: Boolean,
    selling: Currency,
    buying: Currency,
    amount: Double,
    minimalRateSellingToBuying: Double,
    minimumSelled: Double,
    exchangeIf: Option[CurrencyRateOver]
)

final case class CurrencyRateOver(currency: Currency, minimumRate: Double) {

  def isSatisfied(usdRateProvider: UsdRateProvider)(
      implicit ec: ExecutionContext): Future[Boolean] =
    usdRateProvider.currencyUsdRate(currency).map(_ >= minimumRate)
}

final case class OfferCreationData(
    selling: Currency,
    buying: Currency,
    amount: Double,
    minimalRateSellingToBuying: Double,
    minimumSelled: Double,
    exchangeIf: Option[CurrencyRateOver]
)

sealed trait OfferCreationError

object OfferCreationErrors {
  case object CurrenciesMustDiffer extends OfferCreationError
  case object AmountMustBePositive extends OfferCreationError
  case object RateMustBePositive extends OfferCreationError
  case object MinimumSelledMustBePositive extends OfferCreationError
  case object MinimumRateMustBePositive extends OfferCreationError
}

sealed trait OfferExchangeError

object OfferExchangeErrors {
  case object UsersOffersDontMatch extends OfferExchangeError
  case object NoSuchOffer extends OfferExchangeError
}
