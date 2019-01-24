package com.market.e2e.helpers

import akka.http.scaladsl.server.Route
import com.market.e2e.specs.RestSpec

import scala.util.Random

trait OpenEndpointHelpers extends RestSpec {

  protected def createUserWithEmail(email: String = Random.nextString(10),
                                    password: String = "passwd")(
      checkResults: => Any)(implicit endpoint: Route): Any =
    Post("/user/register", Map("email" -> email, "password" -> password)) ~> endpoint ~> check(
      checkResults)

  protected def logIn(email: String, password: String)(checkResults: => Any)(
      implicit endpoint: Route): Any =
    Post("/user/login", Map("email" -> email, "password" -> password)) ~> endpoint ~> check(
      checkResults)
}
