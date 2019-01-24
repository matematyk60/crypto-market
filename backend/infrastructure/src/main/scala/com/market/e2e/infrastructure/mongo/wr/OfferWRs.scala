package com.market.e2e.infrastructure.mongo.wr

import com.market.e2e.domain.offer.{CurrencyRateOver, ExchangeOffer}
import reactivemongo.bson.{BSONDocumentHandler, Macros}

object OfferWRs {
  import CurrencyWRs.currencyHandler

  private implicit val currencyRateOver: BSONDocumentHandler[CurrencyRateOver] =
    Macros.handler[CurrencyRateOver]

  implicit val offerHandler: BSONDocumentHandler[ExchangeOffer] =
    Macros.handler[ExchangeOffer]
}
