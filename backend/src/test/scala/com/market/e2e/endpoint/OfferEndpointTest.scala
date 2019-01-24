package com.market.e2e.endpoint

import com.market.e2e.helpers.{
  OfferEndpointHelpers,
  OpenEndpointHelpers,
  UserEndpointHelpers
}
import org.json4s.JsonAST.JString

class OfferEndpointTest
    extends OpenEndpointHelpers
    with UserEndpointHelpers
    with OfferEndpointHelpers {

  import OfferEndpointTest._

  "POST for /offer" should "add new simplest offer and it should be avalilable in allOffers" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]
      val userId = (responseAsJson \ "userId").extract[Int]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = 233.3
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = 100.0

      addNewOffer(token,
                  sellingCurrency,
                  buyingCurrency,
                  sellingAmount,
                  minimalRateSellingToBuying,
                  minimumSelled) {
        status shouldBe Statuses.OK
        val offerId = (responseAsJson \ "offerId").extract[String]

        getAllOffers(token) {
          status shouldBe Statuses.OK
          val offer = responseAsJson
            .find(offer =>
              (offer \ "offerId").extractOpt[String].contains(offerId))
            .get

          (offer \ "userId").extract[Int] shouldBe userId
          (offer \ "finished").extract[Boolean] shouldBe false
          (offer \ "selling").extract[String] shouldBe sellingCurrency
          (offer \ "buying").extract[String] shouldBe buyingCurrency
          (offer \ "amount").extract[Double] shouldBe sellingAmount
          (offer \ "minimalRateSellingToBuying")
            .extract[Double] shouldBe minimalRateSellingToBuying
          (offer \ "minimumSelled").extract[Double] shouldBe minimumSelled
        }
      }
    }
  }

  it should "add offer and it should be available in singleOffer endpoint" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]
      val userId = (responseAsJson \ "userId").extract[Int]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = 233.3
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = 100.0

      addNewOffer(token,
                  sellingCurrency,
                  buyingCurrency,
                  sellingAmount,
                  minimalRateSellingToBuying,
                  minimumSelled) {
        status shouldBe Statuses.OK
        val offerId = (responseAsJson \ "offerId").extract[String]

        getSingleOffer(token, offerId) {
          status shouldBe Statuses.OK

          (responseAsJson \ "userId").extract[Int] shouldBe userId
          (responseAsJson \ "finished").extract[Boolean] shouldBe false
          (responseAsJson \ "selling").extract[String] shouldBe sellingCurrency
          (responseAsJson \ "buying").extract[String] shouldBe buyingCurrency
          (responseAsJson \ "amount").extract[Double] shouldBe sellingAmount
          (responseAsJson \ "minimalRateSellingToBuying")
            .extract[Double] shouldBe minimalRateSellingToBuying
          (responseAsJson \ "minimumSelled")
            .extract[Double] shouldBe minimumSelled
        }
      }
    }
  }

  it should "add full offer and it should be available in singleOffer endpoint" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]
      val userId = (responseAsJson \ "userId").extract[Int]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = 233.3
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = 100.0
      val currencyRateOverCurrency = "BitCoin"
      val currencyRateOverRate = 331.0D

      addNewOffer(
        token,
        sellingCurrency,
        buyingCurrency,
        sellingAmount,
        minimalRateSellingToBuying,
        minimumSelled,
        currencyRateOverCurrency = Some(currencyRateOverCurrency),
        currencyRateOverRate = Some(currencyRateOverRate)
      ) {
        status shouldBe Statuses.OK
        val offerId = (responseAsJson \ "offerId").extract[String]

        getSingleOffer(token, offerId) {
          status shouldBe Statuses.OK

          println(responseAsJson)

          (responseAsJson \ "userId").extract[Int] shouldBe userId
          (responseAsJson \ "finished").extract[Boolean] shouldBe false
          (responseAsJson \ "selling").extract[String] shouldBe sellingCurrency
          (responseAsJson \ "buying").extract[String] shouldBe buyingCurrency
          (responseAsJson \ "amount").extract[Double] shouldBe sellingAmount
          (responseAsJson \ "minimalRateSellingToBuying")
            .extract[Double] shouldBe minimalRateSellingToBuying
          (responseAsJson \ "minimumSelled")
            .extract[Double] shouldBe minimumSelled
          (responseAsJson \ "exchangeIf" \ "currency")
            .extract[String] shouldBe currencyRateOverCurrency
          (responseAsJson \ "exchangeIf" \ "minimumRate")
            .extract[Double] shouldBe currencyRateOverRate
        }
      }
    }
  }

  it should "return proper error when adds offer with same currencies" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = bitCoinCurrency
      val sellingAmount = 233.3
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = 100.0

      addNewOffer(token,
                  sellingCurrency,
                  buyingCurrency,
                  sellingAmount,
                  minimalRateSellingToBuying,
                  minimumSelled) {
        status shouldBe Statuses.UnprocessableEntity
        (responseAsJson \ "errors").children
          .map(_.extract[String]) should contain("currencies_must_differ")
      }
    }
  }

  it should "return proper error when adds offer with negative amount" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = -100D
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = 100.0

      addNewOffer(token,
                  sellingCurrency,
                  buyingCurrency,
                  sellingAmount,
                  minimalRateSellingToBuying,
                  minimumSelled) {
        status shouldBe Statuses.UnprocessableEntity
        (responseAsJson \ "errors").children
          .map(_.extract[String]) should contain("amount_must_be_positive")
      }
    }
  }

  it should "return proper error when adds offer with negative minimum selled" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = 1000D
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = -100.0

      addNewOffer(token,
                  sellingCurrency,
                  buyingCurrency,
                  sellingAmount,
                  minimalRateSellingToBuying,
                  minimumSelled) {
        status shouldBe Statuses.UnprocessableEntity
        (responseAsJson \ "errors").children
          .map(_.extract[String]) should contain(
          "minimum_selled_must_be_positive")
      }
    }
  }

  it should "return proper error when adds offer with negative rate" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = 1000.0
      val minimalRateSellingToBuying = -123D
      val minimumSelled = 100.0

      addNewOffer(token,
                  sellingCurrency,
                  buyingCurrency,
                  sellingAmount,
                  minimalRateSellingToBuying,
                  minimumSelled) {
        status shouldBe Statuses.UnprocessableEntity
        (responseAsJson \ "errors").children
          .map(_.extract[String]) should contain("rate_must_be_positive")
      }
    }
  }

  it should "return proper error when adds offer with negative minimum rate" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]

      val sellingCurrency = bitCoinCurrency
      val buyingCurrency = litecoinCurrency
      val sellingAmount = 233.3
      val minimalRateSellingToBuying = 1.5
      val minimumSelled = 100.0
      val currencyRateOverCurrency = "BitCoin"
      val currencyRateOverRate = -331.0D

      addNewOffer(
        token,
        sellingCurrency,
        buyingCurrency,
        sellingAmount,
        minimalRateSellingToBuying,
        minimumSelled,
        currencyRateOverCurrency = Some(currencyRateOverCurrency),
        currencyRateOverRate = Some(currencyRateOverRate)
      ) {
        status shouldBe Statuses.UnprocessableEntity
        (responseAsJson \ "errors").children
          .map(_.extract[String]) should contain(
          "minimum_rate_must_be_positive")
      }
    }
  }

  it should "respond with 401 if user performs with invalid token" in {
    addNewOffer(token = "invalid-token",
                selling = bitCoinCurrency,
                buying = litecoinCurrency,
                amount = 2.3,
                minimalRateSellingToBuying = 1.0,
                minimumSelled = 1.3) {
      status shouldBe Statuses.Unauthorized
    }
  }

  "GET for /offer/{offerId}" should "return 404 there is no such offer" in {
    createUserWithEmail() {
      val token = (responseAsJson \ "token").extract[String]

      getSingleOffer(token, offerId = "non-existing-offer") {
        status shouldBe Statuses.NotFound
      }
    }
  }

  it should "respond with 401 if user performer with invalid token" in {
    getSingleOffer(token = "ivalid-token", offerId = "not-important") {
      status shouldBe Statuses.Unauthorized
    }
  }

  "POST for /offer/{offerId}/exchange" should "mark offer as done and user should have updated balance" in {
    createUserWithEmail() {
      val firstToken = (responseAsJson \ "token").extract[String]

      createUserWithEmail() {
        val secondToken = (responseAsJson \ "token").extract[String]

        val sellingCurrency = bitCoinCurrency
        val buyingCurrency = litecoinCurrency
        val sellingAmount = 1000.0
        val minimalRateSellingToBuying = 2
        val minimumSelled = 1000.0

        addNewOffer(firstToken,
                    sellingCurrency,
                    buyingCurrency,
                    sellingAmount,
                    minimalRateSellingToBuying,
                    minimumSelled) {

          val sellingCurrency = litecoinCurrency
          val buyingCurrency = bitCoinCurrency
          val sellingAmount = 2000.0
          val minimalRateSellingToBuying = 0.5
          val minimumSelled = 2000.0

          status shouldBe Statuses.OK

          addNewOffer(
            secondToken,
            sellingCurrency,
            buyingCurrency,
            sellingAmount,
            minimalRateSellingToBuying,
            minimumSelled
          ) {
            status shouldBe Statuses.OK
            val sndOfferId = (responseAsJson \ "offerId").extract[String]
            exchangeWithOffer(firstToken, sndOfferId) {
              status shouldBe Statuses.OK

              getMyFunds(firstToken) {
                val bitCoinObject = responseAsJson.children
                  .find(json =>
                    (json \ "currency").extractOpt[String].contains("BitCoin"))
                  .get
                val litecoinObject = responseAsJson.children
                  .find(json =>
                    (json \ "currency").extractOpt[String].contains("Litecoin"))
                  .get

                (bitCoinObject \ "amount").extract[Double] shouldBe -1000.0
                (litecoinObject \ "amount")
                  .extract[Double] shouldBe (2000.0 * 0.95)

                getMyFunds(secondToken) {
                  val bitCoinObject = responseAsJson.children
                    .find(json =>
                      (json \ "currency")
                        .extractOpt[String]
                        .contains("BitCoin"))
                    .get
                  val litecoinObject = responseAsJson.children
                    .find(json =>
                      (json \ "currency")
                        .extractOpt[String]
                        .contains("Litecoin"))
                    .get

                  (bitCoinObject \ "amount")
                    .extract[Double] shouldBe (1000.0 * 0.95)
                  (litecoinObject \ "amount")
                    .extract[Double] shouldBe (-2000.0)
                }
              }
            }
          }
        }
      }
    }
  }

  it should "not match matching offers if currency rate condition is not satisfied" in {
    createUserWithEmail() {
      val firstToken = (responseAsJson \ "token").extract[String]

      createUserWithEmail() {
        val secondToken = (responseAsJson \ "token").extract[String]

        val sellingCurrency = bitCoinCurrency
        val buyingCurrency = litecoinCurrency
        val sellingAmount = 1000.0
        val minimalRateSellingToBuying = 2
        val minimumSelled = 1000.0
        val currencyRateOverCurrency = "BitCoin"
        val currencyRateOverRate = 10000000

        addNewOffer(
          firstToken,
          sellingCurrency,
          buyingCurrency,
          sellingAmount,
          minimalRateSellingToBuying,
          minimumSelled,
          currencyRateOverCurrency = Some(currencyRateOverCurrency),
          currencyRateOverRate = Some(currencyRateOverRate)
        ) {

          val sellingCurrency = litecoinCurrency
          val buyingCurrency = bitCoinCurrency
          val sellingAmount = 2000.0
          val minimalRateSellingToBuying = 0.5
          val minimumSelled = 2000.0

          status shouldBe Statuses.OK

          addNewOffer(
            secondToken,
            sellingCurrency,
            buyingCurrency,
            sellingAmount,
            minimalRateSellingToBuying,
            minimumSelled
          ) {
            status shouldBe Statuses.OK
            val sndOfferId = (responseAsJson \ "offerId").extract[String]
            exchangeWithOffer(firstToken, sndOfferId) {
              status shouldBe Statuses.UnprocessableEntity
              (responseAsJson \ "errors").children should contain(
                JString("users_offers_dont_match"))
            }
          }
        }
      }
    }
  }

  "Automatic offer matcher" should "match and exchange two matching offers automatically" in {
    createUserWithEmail() {
      val firstToken = (responseAsJson \ "token").extract[String]

      createUserWithEmail() {
        val secondToken = (responseAsJson \ "token").extract[String]

        val sellingCurrency = bitCoinCurrency
        val buyingCurrency = litecoinCurrency
        val sellingAmount = 1000.0
        val minimalRateSellingToBuying = 2
        val minimumSelled = 1000.0

        addNewOffer(firstToken,
                    sellingCurrency,
                    buyingCurrency,
                    sellingAmount,
                    minimalRateSellingToBuying,
                    minimumSelled) {

          val fstOfferId = (responseAsJson \ "offerId").extract[String]

          val sellingCurrency = litecoinCurrency
          val buyingCurrency = bitCoinCurrency
          val sellingAmount = 2000.0
          val minimalRateSellingToBuying = 0.5
          val minimumSelled = 2000.0

          status shouldBe Statuses.OK

          addNewOffer(
            secondToken,
            sellingCurrency,
            buyingCurrency,
            sellingAmount,
            minimalRateSellingToBuying,
            minimumSelled
          ) {
            val sndOfferId = (responseAsJson \ "offerId").extract[String]

            application.exchangeOffers()

            getAllOffers(firstToken) {
              status shouldBe Statuses.OK

              val postedOffers = responseAsJson.children.filter(json => {
                val offerId = (json \ "offerId").extract[String]
                offerId == fstOfferId || offerId == sndOfferId
              })
              postedOffers.foreach(json =>
                (json \ "finished").extract[Boolean] shouldBe true)
            }
          }
        }
      }
    }
  }

}

object OfferEndpointTest {
  val bitCoinCurrency = "BitCoin"
  val litecoinCurrency = "Litecoin"
}
