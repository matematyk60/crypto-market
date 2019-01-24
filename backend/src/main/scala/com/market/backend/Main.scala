package com.market.backend

import java.time.Clock

import com.typesafe.config.ConfigFactory

object Main extends App {
  private val config = ConfigFactory.load("default.conf")

  new Application(config)(Clock.systemDefaultZone()).start()

}
