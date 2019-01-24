package com.market.e2e.infrastructure.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.stream.Materializer
import com.market.e2e.domain.currency.{Currency, Currencys, UsdRateProvider}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class RestUsdRateProvider(blockchainPrefixUrl: String)(
    implicit system: ActorSystem,
    mat: Materializer,
    ec: ExecutionContext)
    extends UsdRateProvider
    with StrictLogging {

  val client = Http()

  val defaultBtcUsdRate = 3500.0
  val defaultLitecoinUsdRate = 29.80

  override def currencyUsdRate(currency: Currency): Future[Double] =
    currency match {
      case Currencys.BitCoin => bitcoinUsdRate()
      case Currencys.Litecoin =>
        Future.successful(defaultLitecoinUsdRate)
    }

  private def bitcoinUsdRate(): Future[Double] = {
    val request = HttpRequest(
      uri = s"$blockchainPrefixUrl/tobtc?currency=USD&value=500"
    )

    client
      .singleRequest(request)
      .map(response => (response.status, response))
      .flatMap {
        case (StatusCodes.OK, req) =>
          req.entity
            .toStrict(30 seconds)
            .map(_.data.utf8String)
            .map(string =>
              Try(string.toDouble) match {
                case Success(oneUsdInBtc) => 1 / oneUsdInBtc
                case Failure(ex) =>
                  logger.error(
                    s"Cannot parse response $string to double, using default rate",
                    ex)
                  defaultBtcUsdRate
            })
        case (otherStatus, _) =>
          logger.error(
            s"Received non-200 [$otherStatus] code, using default rate")
          Future.successful(defaultBtcUsdRate)
      }
  }
}
