package com.market.e2e.domain.transaction

import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.currency.Currency

sealed trait TransactionType

object TransactionTypes {
  case object Credit extends TransactionType
  case object Debit extends TransactionType
}

final case class Transaction(userId: UserId,
                             transactionType: TransactionType,
                             currency: Currency,
                             amount: Double)
