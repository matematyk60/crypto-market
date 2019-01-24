package com.market.e2e.infrastructure.mongo.wr

import com.market.e2e.domain.transaction.Transaction
import reactivemongo.bson.{BSONDocumentHandler, Macros}

object TransactionWRs {

  import CurrencyWRs._

  implicit val transactionHandler: BSONDocumentHandler[Transaction] =
    Macros.handler[Transaction]

}
