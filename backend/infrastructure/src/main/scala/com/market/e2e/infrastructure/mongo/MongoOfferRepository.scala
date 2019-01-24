package com.market.e2e.infrastructure.mongo

import java.util.UUID

import com.market.e2e.domain.TypeAliases.{OfferId, UserId}
import com.market.e2e.domain.offer.{
  ExchangeOffer,
  OfferCreationData,
  OfferRepository
}
import com.market.e2e.infrastructure.mongo.wr.OfferWRs
import hero.mongo.MongoRepository
import reactivemongo.api.{Cursor, DefaultDB}
import reactivemongo.bson.{BSONDocument, BSONString}

import scala.concurrent.{ExecutionContext, Future}

class MongoOfferRepository(db: DefaultDB)(implicit ec: ExecutionContext)
    extends MongoRepository(db, "offerRepository")
    with OfferRepository {

  import OfferWRs._

  override def drop(): Future[Unit] =
    collection.drop(failIfNotFound = false).map(_ => ())

  override def saveOffer(
      userId: UserId,
      offerCreationData: OfferCreationData): Future[String] = {
    val id = UUID.randomUUID().toString
    val offerToSave = ExchangeOffer(
      id,
      userId,
      finished = false,
      offerCreationData.selling,
      offerCreationData.buying,
      offerCreationData.amount,
      offerCreationData.minimalRateSellingToBuying,
      offerCreationData.minimumSelled,
      offerCreationData.exchangeIf
    )
    save(offerToSave).map(_ => id)
  }

  override def getOfferWithId(offerId: OfferId): Future[Option[ExchangeOffer]] =
    getBy("offerId", BSONString(offerId))

  override def markOfferAsFinished(offerId: OfferId): Future[Unit] =
    collection
      .update(BSONDocument("offerId" -> offerId),
              BSONDocument("$set" -> BSONDocument("finished" -> true)))
      .map(_ => ())

  override def getUserOffers(userId: UserId): Future[List[ExchangeOffer]] =
    collection
      .find(BSONDocument("userId" -> userId))
      .cursor()
      .collect(-1, Cursor.FailOnError[List[ExchangeOffer]]())

  override def getAllOffers: Future[List[ExchangeOffer]] =
    collection
      .find(BSONDocument.empty)
      .cursor()
      .collect(-1, Cursor.FailOnError[List[ExchangeOffer]]())

  override def getAllNotFinishedOffers: Future[List[ExchangeOffer]] =
    collection
      .find(BSONDocument("finished" -> false))
      .cursor()
      .collect(-1, Cursor.FailOnError[List[ExchangeOffer]]())

}
