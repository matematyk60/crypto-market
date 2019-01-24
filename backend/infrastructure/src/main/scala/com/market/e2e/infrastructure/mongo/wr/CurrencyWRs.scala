package com.market.e2e.infrastructure.mongo.wr

import com.market.e2e.domain.currency.Currency
import com.market.e2e.domain.currency.Currencys.{BitCoin, Litecoin}
import com.market.e2e.domain.transaction.TransactionType
import com.market.e2e.domain.transaction.TransactionTypes.{Credit, Debit}
import reactivemongo.bson.{BSONHandler, BSONString}

object CurrencyWRs {

  implicit val currencyHandler: BSONHandler[BSONString, Currency] =
    BSONHandler({
      case BSONString("BitCoin")  => BitCoin
      case BSONString("Litecoin") => Litecoin
    }, {
      case BitCoin  => BSONString("BitCoin")
      case Litecoin => BSONString("Litecoin")
    })

  implicit val transactionTypeHandler
    : BSONHandler[BSONString, TransactionType] = BSONHandler({
    case BSONString("Credit") => Credit
    case BSONString("Debit")  => Debit
  }, {
    case Credit => BSONString("Credit")
    case Debit  => BSONString("Debit")
  })
}
