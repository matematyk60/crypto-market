package com.market.e2e.domain.transaction

import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.currency.Currency

import scala.concurrent.Future

trait TransactionRepository {
  def saveNewTransaction(transaction: Transaction): Future[Unit]
  def getUsersTransactions(userId: UserId): Future[List[Transaction]]
  def getUsersTransactionsInCurrency(
      userId: UserId,
      currency: Currency): Future[List[Transaction]]
}
