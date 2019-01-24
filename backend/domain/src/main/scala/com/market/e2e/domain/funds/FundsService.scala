package com.market.e2e.domain.funds

import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.currency.{Currency, Currencys}
import com.market.e2e.domain.transaction.TransactionTypes.{Credit, Debit}
import com.market.e2e.domain.transaction.{
  Transaction,
  TransactionRepository,
  TransactionTypes
}

import scala.concurrent.{ExecutionContext, Future}

class FundsService(transactionRepository: TransactionRepository)(
    implicit ec: ExecutionContext) {

  def usersAccountBalance(userId: UserId): Future[List[Funds]] =
    transactionRepository
      .getUsersTransactions(userId)
      .map(
        transactions =>
          (transactions ++ zeroTransactions(userId))
            .groupBy(_.currency)
            .map {
              case (currency, transactionsInCurrency) =>
                Funds(currency, sumTransactions(transactionsInCurrency))
            }
            .toList)

  def usersAccountBalanceInCurrency(userId: UserId,
                                    currency: Currency): Future[Funds] =
    transactionRepository
      .getUsersTransactionsInCurrency(userId, currency)
      .map(
        transactions =>
          Funds(currency,
                sumTransactions(transactions ++ zeroTransactions(userId))))

  def addFundsToUserAccount(userId: UserId,
                            currency: Currency,
                            amount: Double): Future[Unit] = {
    val transaction =
      Transaction(userId, transactionType = Debit, currency, amount)
    transactionRepository.saveNewTransaction(transaction)
  }

  private def sumTransactions(transactions: List[Transaction]): Double =
    transactions
      .map(transaction =>
        transaction.transactionType match {
          case TransactionTypes.Credit => -transaction.amount
          case TransactionTypes.Debit  => transaction.amount
      })
      .sum

  private def zeroTransactions(userId: UserId) =
    (Currencys.BitCoin :: Currencys.Litecoin :: Nil).map(currency =>
      Transaction(userId, TransactionTypes.Credit, currency, amount = 0D))
}
