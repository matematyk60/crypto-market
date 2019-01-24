package com.market.backend

import java.time.Duration
import java.util.concurrent.TimeUnit

import Application.ApplicationConfig
import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration

object ConfigValueLoader {

  def provide(config: Config) =
    ConfigValues(
      ApplicationConfig(
        config.getString("application.bind-host"),
        config.getInt("application.bind-port"),
        config.getString("application.log-level")
      ),
      AuthenticationConfig(
        config.getDuration("authentication.token-duration").asScala,
        config.getString("authentication.private-key-file"),
        config.getString("authentication.public-key-file"),
      ),
      MongoConfig(
        config.getString("mongo.host"),
        config.getInt("mongo.port"),
        config.getString("mongo.db-name")
      ),
      BlockchainInfoServiceConfig(
        config.getString("blockchain-info-service.url-prefix")
      )
    )

  final case class ConfigValues(
      applicationConfig: ApplicationConfig,
      authenticationConfig: AuthenticationConfig,
      mongoConfig: MongoConfig,
      blockchainInfoServiceConfig: BlockchainInfoServiceConfig
  )

  final case class BlockchainInfoServiceConfig(
      blockchainInfoServiceUrlPrefix: String
  )

  final case class MongoConfig(
      host: String,
      port: Int,
      databaseName: String
  ) {
    def uri: String = s"mongodb://$host:$port/$databaseName"
  }

  final case class AuthenticationConfig(tokenDuration: FiniteDuration,
                                        privateKeyFile: String,
                                        publicKeyFile: String)

  private implicit class DurationExt(duration: Duration) {
    def asScala: FiniteDuration =
      FiniteDuration(duration.toNanos, TimeUnit.NANOSECONDS)
  }
}
