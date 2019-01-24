package com.market.e2e.domain.offer

import com.market.e2e.domain.TypeAliases.{OfferId, UserId}

import scala.concurrent.Future

trait OfferRepository {
  def drop(): Future[Unit]
  def saveOffer(userId: UserId,
                offerCreationData: OfferCreationData): Future[String]
  def getOfferWithId(offerId: OfferId): Future[Option[ExchangeOffer]]
  def markOfferAsFinished(offerId: OfferId): Future[Unit]
  def getUserOffers(userId: UserId): Future[List[ExchangeOffer]]
  def getAllOffers: Future[List[ExchangeOffer]]
  def getAllNotFinishedOffers: Future[List[ExchangeOffer]]
}
