package com.market.e2e.infrastructure.mongo

import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.currency.Currency
import com.market.e2e.domain.transaction.{Transaction, TransactionRepository}
import com.market.e2e.infrastructure.mongo.wr.TransactionWRs
import hero.mongo.MongoRepository
import reactivemongo.api.{Cursor, DefaultDB}
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

class MongoTransactionRepository(db: DefaultDB)(implicit ec: ExecutionContext)
    extends MongoRepository(db, "transaction_repository")
    with TransactionRepository {

  import com.market.e2e.infrastructure.mongo.wr.TransactionWRs._
  import com.market.e2e.infrastructure.mongo.wr.CurrencyWRs._

  override def saveNewTransaction(transaction: Transaction): Future[Unit] =
    save(transaction)

  override def getUsersTransactions(userId: UserId): Future[List[Transaction]] =
    collection
      .find(BSONDocument("userId" -> userId))
      .cursor()
      .collect(-1, Cursor.FailOnError[List[Transaction]]())

  override def getUsersTransactionsInCurrency(
      userId: UserId,
      currency: Currency): Future[List[Transaction]] =
    collection
      .find(BSONDocument("userId" -> userId, "currency" -> currency))
      .cursor()
      .collect(-1, Cursor.FailOnError[List[Transaction]]())
}
