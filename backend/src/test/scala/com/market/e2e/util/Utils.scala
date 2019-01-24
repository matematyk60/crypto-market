package com.market.e2e.util

import scala.util.Random

object Utils {

  def randomEmail = s"${Random.nextString(10)}@gmail.com"

}
