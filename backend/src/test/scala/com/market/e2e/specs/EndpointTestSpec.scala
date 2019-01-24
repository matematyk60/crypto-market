package com.market.e2e.specs

import java.time.{Clock, ZonedDateTime}

import com.market.backend.Application
import com.market.e2e.mock.ClockMock
import com.market.e2e.specs.AuthenticationSpec
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import com.typesafe.scalalogging.StrictLogging
import hero.test.mongo.Mongo
import org.scalatest._

import scala.language.postfixOps
import scala.util.Random

trait EndpointTestSpec
    extends FlatSpec
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with RestSpec
    with AuthenticationSpec
    with StrictLogging {

  private val bindPort = 8000 + Random.nextInt(1000)
  private val mongoPort = 1000 + Random.nextInt(1000)

  var application: Application = _
  implicit val clockMock: ClockMock = new ClockMock(Clock.systemDefaultZone())

  def currentTime: ZonedDateTime = ZonedDateTime.now(clockMock)

  protected implicit val config: Config =
    ConfigFactory
      .load("test.conf")
      .withValue("application.bind-port",
                 ConfigValueFactory.fromAnyRef(bindPort))
      .withValue("mongo.host", ConfigValueFactory.fromAnyRef("localhost"))
      .withValue("mongo.port", ConfigValueFactory.fromAnyRef(mongoPort))
      .withAuthenticationConfig

  override protected def beforeAll(): Unit = {
    Mongo.start(mongoPort)
    application = new Application(config)
    application.start()
    createEndpoint(application.routes)
  }

  override protected def afterAll(): Unit = {
    application.stop()
    Mongo.stop()
  }

  override protected def beforeEach(): Unit = {
    clockMock.reset()
    application.drop()
  }
}
