package com.market.e2e.domain.offer

import akka.Done
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import cats.data.EitherT
import com.market.e2e.domain.TypeAliases.{OfferId, UserId}
import com.market.e2e.domain.offer.OfferCreationErrors._
import com.market.e2e.domain.offer.OfferExchangeErrors.{
  NoSuchOffer,
  UsersOffersDontMatch
}
import cats.instances.future.catsStdInstancesForFuture
import com.market.e2e.domain.transaction.{
  Transaction,
  TransactionGenerator,
  TransactionRepository
}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}

class OfferService(offerRepository: OfferRepository,
                   offerMatcher: OfferMatcher,
                   transactionRepository: TransactionRepository)(
    implicit ec: ExecutionContext,
    mat: Materializer)
    extends StrictLogging {

  def createOffer(userId: UserId, offerCreationData: OfferCreationData)
    : Future[Either[OfferCreationError, OfferId]] =
    (for {
      _ <- Either.cond(offerCreationData.amount > 0, (), AmountMustBePositive)
      _ <- Either.cond(offerCreationData.buying != offerCreationData.selling,
                       (),
                       CurrenciesMustDiffer)
      _ <- Either.cond(offerCreationData.minimumSelled > 0,
                       (),
                       MinimumSelledMustBePositive)
      _ <- Either.cond(offerCreationData.minimalRateSellingToBuying > 0,
                       (),
                       RateMustBePositive)
      _ <- Either.cond(offerCreationData.exchangeIf.forall(_.minimumRate > 0),
                       (),
                       MinimumRateMustBePositive)
    } yield ()) match {
      case Left(error) => Future.successful(Left(error))
      case Right(_) =>
        offerRepository.saveOffer(userId, offerCreationData).map(Right(_))
    }

  def exchangeWithOffer(
      userId: UserId,
      offerId: OfferId): Future[Either[OfferExchangeError, Unit]] =
    (for {
      offer <- EitherT.fromOptionF(offerRepository.getOfferWithId(offerId),
                                   ifNone = NoSuchOffer)
      usersOffers <- EitherT
        .liftF[Future, OfferExchangeError, List[ExchangeOffer]](
          offerRepository.getUserOffers(userId))
      matchingOffer <- EitherT
        .fromOptionF[Future, OfferExchangeError, ExchangeOffer](
          findMatchingOffer(offer, usersOffers),
          ifNone = UsersOffersDontMatch)

      transactionsToMake = TransactionGenerator.generateTransactions(
        offer,
        matchingOffer)

      _ <- EitherT.liftF[Future, OfferExchangeError, Done](
        doTransactions(transactionsToMake))

      _ <- EitherT.liftF[Future, OfferExchangeError, Unit](
        offerRepository.markOfferAsFinished(offer.offerId))
      _ <- EitherT.liftF[Future, OfferExchangeError, Unit](
        offerRepository.markOfferAsFinished(matchingOffer.offerId))
    } yield ()).value

  def matchOffersAndExchange(): Future[Done] = {
    (for {
      allNotFinishedOffers <- offerRepository.getAllNotFinishedOffers
      matchingPairs <- matchingOffers(allNotFinishedOffers)
    } yield matchingPairs).flatMap(
      Source(_)
        .mapAsync(4) {
          case (first, second) =>
            val transactionsToMake =
              TransactionGenerator.generateTransactions(first, second)
            doTransactions(transactionsToMake).flatMap(_ =>
              for {
                _ <- offerRepository.markOfferAsFinished(first.offerId)
                _ <- offerRepository.markOfferAsFinished(second.offerId)
              } yield Done)
        }
        .runWith(Sink.ignore))
  }

  private def matchingOffers(offers: List[ExchangeOffer])
    : Future[List[(ExchangeOffer, ExchangeOffer)]] = {
    def matchOffersAndFinishHelper(
        notTargeted: List[ExchangeOffer],
        pairsList: List[(ExchangeOffer, ExchangeOffer)])
      : Future[List[(ExchangeOffer, ExchangeOffer)]] = notTargeted match {
      case headOffer :: tail =>
        findMatchingOffer(headOffer, notTargeted)
          .flatMap {
            case Some(matchingOffer) =>
              val matched = List(headOffer, matchingOffer)
              matchOffersAndFinishHelper(
                tail.filter(target =>
                  matched.forall(_.offerId != target.offerId)),
                (headOffer, matchingOffer) :: pairsList)
            case None =>
              matchOffersAndFinishHelper(tail, pairsList)
          }
      case Nil => Future.successful(pairsList)
    }
    matchOffersAndFinishHelper(offers, List.empty)
  }

  private def doTransactions(transactions: List[Transaction]): Future[Done] =
    Source(
      transactions
    ).mapAsync(4)(transactionRepository.saveNewTransaction)
      .runWith(Sink.ignore)

  private def findMatchingOffer(offer: ExchangeOffer,
                                userOffers: List[ExchangeOffer]) = {
    logger.error(s"JA JEBIE TO $offer, $userOffers")
    Source(userOffers)
      .runFoldAsync(None: Option[ExchangeOffer]) {
        case (some @ Some(_), _) => Future.successful(some)
        case (None, userOffer) =>
          offerMatcher
            .areMatching(offer, userOffer)
            .map(matches => if (matches) Some(userOffer) else None)
      }
  }
}
