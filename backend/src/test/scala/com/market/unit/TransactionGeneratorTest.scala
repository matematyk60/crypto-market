package com.market.unit

import com.market.e2e.domain.currency.{Currency, Currencys}
import com.market.e2e.domain.currency.Currencys.{BitCoin, Litecoin}
import com.market.e2e.domain.offer.{CurrencyRateOver, ExchangeOffer}
import com.market.e2e.domain.transaction.{
  Transaction,
  TransactionGenerator,
  TransactionTypes
}
import org.scalatest.{FlatSpec, Matchers}

class TransactionGeneratorTest extends FlatSpec with Matchers {
  import TransactionGeneratorTest._

  "TransactionGenerator" should "generate proper transactions" in {
    val firstUserId = 10
    val firstOffer =
      offer(firstUserId,
            selling = BitCoin,
            buying = Litecoin,
            1000.0,
            2,
            1000.0,
            exchangeIf = None)

    val secondUserId = 20
    val secondOffer =
      offer(secondUserId, selling = Litecoin, buying = BitCoin, 2000, 0.5, 2000)

    val generatedTransactions =
      TransactionGenerator.generateTransactions(firstOffer, secondOffer)

    generatedTransactions should contain(
      Transaction(0, TransactionTypes.Debit, Currencys.BitCoin, 50.0))
    generatedTransactions should contain(
      Transaction(0, TransactionTypes.Debit, Currencys.Litecoin, 2000 * 0.05))

    generatedTransactions should contain(
      Transaction(firstUserId,
                  TransactionTypes.Credit,
                  Currencys.BitCoin,
                  1000))
    generatedTransactions should contain(
      Transaction(firstUserId,
                  TransactionTypes.Debit,
                  Currencys.Litecoin,
                  2000 * 0.95))

    generatedTransactions should contain(
      Transaction(secondUserId,
                  TransactionTypes.Credit,
                  Currencys.Litecoin,
                  2000))
    generatedTransactions should contain(
      Transaction(secondUserId,
                  TransactionTypes.Debit,
                  Currencys.BitCoin,
                  1000 * 0.95))
  }

  it should "generate proper transactions for not exacly matching transactions" in {
    val firstUserId = 10
    val firstOffer =
      offer(firstUserId,
            selling = BitCoin,
            buying = Litecoin,
            10000.0,
            2,
            0,
            exchangeIf = None)

    val secondUserId = 20
    val secondOffer =
      offer(secondUserId,
            selling = Litecoin,
            buying = BitCoin,
            1000,
            0.125,
            1000)

    val generatedTransactions =
      TransactionGenerator.generateTransactions(firstOffer, secondOffer)

    generatedTransactions should contain(
      Transaction(0, TransactionTypes.Debit, Currencys.BitCoin, 200 * 0.05))
    generatedTransactions should contain(
      Transaction(0, TransactionTypes.Debit, Currencys.Litecoin, 1000 * 0.05))

    generatedTransactions should contain(
      Transaction(firstUserId, TransactionTypes.Credit, Currencys.BitCoin, 200))
    generatedTransactions should contain(
      Transaction(firstUserId,
                  TransactionTypes.Debit,
                  Currencys.Litecoin,
                  1000 * 0.95))

    generatedTransactions should contain(
      Transaction(secondUserId,
                  TransactionTypes.Credit,
                  Currencys.Litecoin,
                  1000))
    generatedTransactions should contain(
      Transaction(secondUserId,
                  TransactionTypes.Debit,
                  Currencys.BitCoin,
                  200 * 0.95))
  }

}

object TransactionGeneratorTest {
  def offer(userId: Int,
            selling: Currency,
            buying: Currency,
            amount: Double,
            minimalRateSellingToBuying: Double,
            minimumSelled: Double,
            exchangeIf: Option[CurrencyRateOver] = None) =
    ExchangeOffer("",
                  userId,
                  finished = false,
                  selling,
                  buying,
                  amount,
                  minimalRateSellingToBuying,
                  minimumSelled,
                  exchangeIf)
}
