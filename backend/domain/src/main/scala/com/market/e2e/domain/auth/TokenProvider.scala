package com.market.e2e.domain.auth

import scala.concurrent.Future

trait TokenProvider {
  def generateToken(id: Int): Future[String]
}
