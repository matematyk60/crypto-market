package com.market.e2e.infrastructure.mongo.wr

import com.market.e2e.infrastructure.mongo.MongoUserRepository.UserDTO
import com.market.e2e.domain.currency.Currency
import com.market.e2e.domain.currency.Currencys.{BitCoin, Litecoin}
import com.market.e2e.domain.offer.ExchangeOffer
import reactivemongo.bson.{BSONDocumentHandler, BSONHandler, BSONString, Macros}

object UserWRs {

  implicit val userWRs: BSONDocumentHandler[UserDTO] = Macros.handler[UserDTO]
}
