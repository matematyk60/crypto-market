package com.market.e2e.endpoint

import com.market.e2e.helpers.{OpenEndpointHelpers, UserEndpointHelpers}
import com.market.e2e.specs.EndpointTestSpec

class UserEndpointTest
    extends EndpointTestSpec
    with OpenEndpointHelpers
    with UserEndpointHelpers {

  "GET for /user/me/funds" should "return zero for all currencies right after creation" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      val token = (responseAsJson \ "token").extract[String]

      getMyFunds(token) {
        status shouldBe Statuses.OK
        val bitCoinObject = responseAsJson.children
          .find(json =>
            (json \ "currency").extractOpt[String].contains("BitCoin"))
          .get
        val litecoinObject = responseAsJson.children
          .find(json =>
            (json \ "currency").extractOpt[String].contains("Litecoin"))
          .get

        (bitCoinObject \ "amount").extract[Double] shouldBe 0D
        (litecoinObject \ "amount").extract[Double] shouldBe 0D
      }
    }
  }

  it should "return 401 if user has invalid token" in {
    getMyFunds(token = "invalid-token") {
      status shouldBe Statuses.Unauthorized
    }
  }

  "GET for /user/me/funds/currency" should "return users balance" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      val token = (responseAsJson \ "token").extract[String]

      getMyFundsInCurrency(token, "BitCoin") {
        status shouldBe Statuses.OK
        (responseAsJson \ "currency").extractOpt[String].contains("BitCoin")

        (responseAsJson \ "amount").extract[Double] shouldBe 0D
      }
    }
  }

  it should "return 400 for unknown currency" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      val token = (responseAsJson \ "token").extract[String]

      getMyFundsInCurrency(token, "ugabuga") {
        status shouldBe Statuses.BadRequest
      }
    }
  }

  it should "return 401 if user has invalid token" in {
    getMyFundsInCurrency("invalid_token", "BitCoin") {
      status shouldBe Statuses.Unauthorized
    }
  }

  "POST for /user/me/funds/currency" should "add BitCoin funds to user's account" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      val token = (responseAsJson \ "token").extract[String]
      val currency = "BitCoin"
      val amount = 50.4

      addFunds(token, currency, amount) {
        status shouldBe Statuses.OK

        getMyFunds(token) {
          status shouldBe Statuses.OK
          val bitCoinObject = responseAsJson.children
            .find(json =>
              (json \ "currency").extractOpt[String].contains(currency))
            .get

          (bitCoinObject \ "amount").extract[Double] shouldBe amount
        }
      }
    }
  }

  it should "add Litecoin funds to user's account" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      val token = (responseAsJson \ "token").extract[String]
      val currency = "Litecoin"
      val amount = 50.4

      addFunds(token, currency, amount) {
        status shouldBe Statuses.OK

        getMyFunds(token) {
          status shouldBe Statuses.OK
          val litecoinObject = responseAsJson.children
            .find(json =>
              (json \ "currency").extractOpt[String].contains(currency))
            .get

          (litecoinObject \ "amount").extract[Double] shouldBe amount
        }
      }
    }
  }

  it should "add Litecoin and BitCoin funds to user's account" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      val token = (responseAsJson \ "token").extract[String]
      val litecoinCurrency = "Litecoin"
      val litecoinAmount = 50.4

      val bitCoinCurrency = "BitCoin"
      val bitCoinAmount = 223.3

      addFunds(token, litecoinCurrency, litecoinAmount) {
        status shouldBe Statuses.OK

        addFunds(token, bitCoinCurrency, bitCoinAmount) {
          status shouldBe Statuses.OK

          getMyFunds(token) {
            status shouldBe Statuses.OK
            val bitCoinObject = responseAsJson.children
              .find(
                json =>
                  (json \ "currency")
                    .extractOpt[String]
                    .contains(bitCoinCurrency))
              .get

            (bitCoinObject \ "amount").extract[Double] shouldBe bitCoinAmount

            val litecoinObject = responseAsJson.children
              .find(
                json =>
                  (json \ "currency")
                    .extractOpt[String]
                    .contains(litecoinCurrency))
              .get

            (litecoinObject \ "amount").extract[Double] shouldBe litecoinAmount
          }
        }
      }
    }
  }

  it should "return 401 if user has invalid token" in {
    getMyFunds(token = "invalid-token") {
      status shouldBe Statuses.Unauthorized
    }
  }
}
