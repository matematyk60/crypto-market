package com.market.backend

import java.time.Clock

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ch.qos.logback.classic.{Level, Logger}
import com.market.backend.ConfigValueLoader.ConfigValues
import com.market.e2e.api.core.{Endpoint, EndpointsWrapper}
import com.market.e2e.api.jwt.{JwtAuthenticator, JwtTokenProvider}
import com.market.e2e.api.offer.OfferEndpoint
import com.market.e2e.api.open.OpenEndpoint
import com.market.e2e.api.user.UserEndpoint
import com.market.e2e.domain.funds.FundsService
import com.market.e2e.domain.offer.{
  OfferMatcher,
  OfferMatchingScheduler,
  OfferService
}
import com.market.e2e.domain.user.{UserDetails, UserService}
import com.market.e2e.infrastructure.mongo.{
  MongoOfferRepository,
  MongoTransactionRepository,
  MongoUserRepository
}
import com.market.e2e.infrastructure.rest.RestUsdRateProvider
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import hero.crypto.KeyReaders.{PrivateKeyReader, PublicKeyReader}
import org.slf4j.LoggerFactory
import reactivemongo.api.FailoverStrategy.FactorFun
import reactivemongo.api.{FailoverStrategy, MongoConnection, MongoDriver}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class Application(config: Config)(implicit clock: Clock) extends StrictLogging {
  private implicit val system: ActorSystem =
    ActorSystem("CryptoMarket", config)
  private implicit val dispatcher: ExecutionContext = system.dispatcher
  private implicit val mat: ActorMaterializer = ActorMaterializer()

  private val ConfigValues(
    applicationConfig,
    authenticationConfig,
    mongoConfig,
    blockchainInfoServiceConfig
  ) =
    ConfigValueLoader.provide(config)

  LoggerFactory
    .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    .asInstanceOf[Logger]
    .setLevel(Level.valueOf(applicationConfig.logLevel))

  private val mongoDriver = MongoDriver()
  private val mongoConnection =
    MongoConnection.parseURI(mongoConfig.uri).map(mongoDriver.connection)
  private val failoverStrategy = FailoverStrategy(initialDelay = 300 millis,
                                                  retries = 20,
                                                  delayFactor = FactorFun(1.5))
  private val mongoDb = Await.result(
    Future
      .fromTry(mongoConnection)
      .flatMap(_.database(mongoConfig.databaseName, failoverStrategy)),
    30 seconds)

  private val usdRateProvider = new RestUsdRateProvider(
    blockchainInfoServiceConfig.blockchainInfoServiceUrlPrefix)

  private val offerMatcher = new OfferMatcher(usdRateProvider)

  private val mongoOfferRepository = new MongoOfferRepository(mongoDb)
  private val mongoTransactionRepository = new MongoTransactionRepository(
    mongoDb)
  private val mongoUserRepository = new MongoUserRepository(mongoDb)
  Await.result(
    mongoUserRepository.saveNewUser(UserDetails("system@crypto.com", "passwd"),
                                    id = Some(0)),
    10 seconds)

  private val fundsService = new FundsService(mongoTransactionRepository)
  private val userService =
    new UserService(mongoUserRepository, mongoTransactionRepository)
  private val offerService =
    new OfferService(mongoOfferRepository,
                     offerMatcher,
                     mongoTransactionRepository)

  private val tokenProvider =
    new JwtTokenProvider(
      PrivateKeyReader.get(authenticationConfig.privateKeyFile),
      authenticationConfig.tokenDuration)

  private val jwtAuthenticator = new JwtAuthenticator(
    PublicKeyReader.get(authenticationConfig.publicKeyFile))

  private val userEndpoint = new UserEndpoint(jwtAuthenticator, fundsService)
  private val openEndpoint =
    new OpenEndpoint(mongoUserRepository, userService, tokenProvider)
  private val offerEndpoint =
    new OfferEndpoint(jwtAuthenticator, offerService, mongoOfferRepository)
  private val offerMatcherScheduler =
    new OfferMatchingScheduler(offerService)

  val routes: Route = new EndpointsWrapper(
    userEndpoint,
    openEndpoint,
    offerEndpoint
  ).routingWithSupport

  def start(): Unit = {
    Endpoint.start(applicationConfig.bindHost,
                   applicationConfig.bindPort,
                   routes)
    offerMatcherScheduler.scheduleOfferMatching(1 minute)
  }

  def stop(): Unit = {
    Await.result(for {
      _ <- mongoDb.connection.askClose()(30 seconds)
      _ <- system.terminate()

    } yield (), 30 seconds)
  }

  def drop(): Unit = {
    Await.result(mongoOfferRepository.drop(), 10 seconds)
  }

  def exchangeOffers(): Unit =
    Await.result(offerService.matchOffersAndExchange(), 20 seconds)
}

object Application {
  final case class ApplicationConfig(bindHost: String,
                                     bindPort: Int,
                                     logLevel: String)
}
