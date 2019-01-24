package com.market.unit

import com.market.e2e.domain.currency.{Currency, Currencys}
import com.market.e2e.domain.offer.{CurrencyRateOver, ExchangeOffer}
import com.market.unit.mock.OfferMatcherMocks
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class OfferMatcherTest extends FlatSpec with Matchers {

  import Currencys._
  import OfferMatcherTest._

  "OfferMatcher" should "match two ideally matching offers" in {

    val offerMatcher = OfferMatcherMocks.noExchangeIfMatcher
    val firstOffer =
      offer(selling = BitCoin, buying = Litecoin, 1000.0, 2, 1000.0)
    val secondOffer =
      offer(selling = Litecoin, buying = BitCoin, 2000, 0.5, 2000)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe true
  }

  it should "match two ideally matching offers in reversal" in {

    val offerMatcher = OfferMatcherMocks.noExchangeIfMatcher
    val firstOffer =
      offer(selling = Litecoin, buying = BitCoin, 1000.0, 2, 1000.0)
    val secondOffer =
      offer(selling = BitCoin, buying = Litecoin, 2000, 0.5, 2000)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe true
  }

  it should "match not ideally matching offers" in {
    val offerMatcher = OfferMatcherMocks.noExchangeIfMatcher
    val firstOffer =
      offer(selling = BitCoin, buying = Litecoin, 1000.0, 2, 1000.0)
    val secondOffer =
      offer(selling = Litecoin, buying = BitCoin, 10000D, 0.2, 2000)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe true
  }

  it should "not match offers with not matching currencies" in {
    val offerMatcher = OfferMatcherMocks.noExchangeIfMatcher
    val firstOffer =
      offer(selling = BitCoin, buying = Litecoin, 0, 0, 0)
    val secondOffer =
      offer(selling = Litecoin, buying = Litecoin, 0, 0, 0)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe false
  }

  it should "not match two offers with not matching rates" in {
    val offerMatcher = OfferMatcherMocks.noExchangeIfMatcher
    val firstOffer =
      offer(selling = BitCoin, buying = Litecoin, 1000.0, 2, 0)
    val secondOffer =
      offer(selling = Litecoin, buying = BitCoin, 2000, 0.6, 0)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe false
  }

  "OfferMatcber with USD rate provider" should "match matching offers if exchangeIf is satisfied" in {
    val bitCoinRate = 1001D
    val offerMatcher =
      OfferMatcherMocks.exchangeIfMatcher(bitcoinUsdRateOver = bitCoinRate)
    val firstOffer =
      offer(selling = BitCoin,
            buying = Litecoin,
            1000.0,
            2,
            1000.0,
            exchangeIf = Some(CurrencyRateOver(BitCoin, bitCoinRate)))
    val secondOffer =
      offer(selling = Litecoin, buying = BitCoin, 2000, 0.5, 2000)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe true
  }

  it should "match matching offers if both exchangeIfs are satisfied" in {
    val bitCoinRate = 1001D
    val offerMatcher =
      OfferMatcherMocks.exchangeIfMatcher(bitcoinUsdRateOver = bitCoinRate)
    val firstOffer =
      offer(selling = BitCoin,
            buying = Litecoin,
            1000.0,
            2,
            1000.0,
            exchangeIf = Some(CurrencyRateOver(BitCoin, bitCoinRate)))
    val secondOffer =
      offer(selling = Litecoin,
            buying = BitCoin,
            2000,
            0.5,
            2000,
            exchangeIf = Some(CurrencyRateOver(BitCoin, bitCoinRate - 100D)))

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe true
  }

  it should "not match offers when one's exchangeIf is not satisfied" in {
    val bitCoinRate = 1000D
    val offerMatcher =
      OfferMatcherMocks.exchangeIfMatcher(bitcoinUsdRateOver = bitCoinRate)
    val firstOffer =
      offer(selling = BitCoin,
            buying = Litecoin,
            1000.0,
            2,
            1000.0,
            exchangeIf = Some(CurrencyRateOver(BitCoin, bitCoinRate + 1000D)))
    val secondOffer =
      offer(selling = Litecoin, buying = BitCoin, 2000, 0.5, 2000)

    offerMatcher
      .areMatching(firstOffer, secondOffer)
      .tillCompletes shouldBe false
  }

}

object OfferMatcherTest {

  implicit class FutureCompleter[T](future: Future[T]) {
    def tillCompletes: T = Await.result(future, 10 seconds)
  }

  def offer(selling: Currency,
            buying: Currency,
            amount: Double,
            minimalRateSellingToBuying: Double,
            minimumSelled: Double,
            exchangeIf: Option[CurrencyRateOver] = None) =
    ExchangeOffer(offerId = "",
                  userId = 0,
                  finished = false,
                  selling,
                  buying,
                  amount,
                  minimalRateSellingToBuying,
                  minimumSelled,
                  exchangeIf)
}
