package com.market.e2e.endpoint

import com.market.e2e.helpers.OpenEndpointHelpers
import com.market.e2e.specs.EndpointTestSpec
import com.market.e2e.util.Utils.randomEmail

class OpenEndpointTest extends EndpointTestSpec with OpenEndpointHelpers {

  "POST for /users" should "create user and return token" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      (responseAsJson \ "token").extractOpt[String] shouldBe defined
    }
  }

  it should "return his funds after creation" in {
    createUserWithEmail() {
      status shouldBe Statuses.OK
      (responseAsJson \ "token").extractOpt[String] shouldBe defined
    }
  }

  "POST for /users/login" should "allow registered user to log in" in {
    val email = randomEmail
    val password = "passwd"
    createUserWithEmail(email, password) {
      status shouldBe Statuses.OK

      logIn(email, password) {
        status shouldBe Statuses.OK
        (responseAsJson \ "token").extractOpt[String] shouldBe defined
      }
    }
  }

  it should "return 401 if user provides invalid password" in {
    val email = randomEmail
    val password = "passwd"
    createUserWithEmail(email, password = "invalid-password") {
      status shouldBe Statuses.OK

      logIn(email, password) {
        status shouldBe Statuses.Unauthorized
      }
    }
  }

  it should "return 401 when user tries to login with invalid credentials" in {
    val email = "incorrect@email.com"
    val password = "passwd"

    logIn(email, password) {
      status shouldBe Statuses.Unauthorized
    }
  }
}
