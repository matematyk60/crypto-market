package com.market.e2e.domain.offer

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

class OfferMatchingScheduler(offerService: OfferService)(
    implicit ec: ExecutionContext,
    mat: Materializer)
    extends StrictLogging {

  def scheduleOfferMatching(interval: FiniteDuration): Unit =
    Source
      .tick(interval, interval, ())
      .mapAsync(1)(_ => offerService.matchOffersAndExchange())
      .runWith(Sink.ignore)
      .onComplete {
        case Failure(exception) =>
          logger.error(
            "Offer matching and exchanging stream failed, restarting...",
            exception)
          scheduleOfferMatching(interval)
        case Success(_) =>
          logger.info(
            "Offer matching and exchanging stream succeeded, restarting")
          scheduleOfferMatching(interval)
      }

}
