package com.market.e2e.api.offer

import akka.http.scaladsl.server.Route
import com.market.e2e.api.CurrencySerializers
import com.market.e2e.api.core.EndpointWithAuthentication
import com.market.e2e.api.jwt.JwtAuthenticator
import com.market.e2e.domain.offer.{
  OfferCreationData,
  OfferRepository,
  OfferService
}
import org.json4s.{DefaultFormats, Formats}

import scala.concurrent.ExecutionContext

class OfferEndpoint(
    jwtAuthenticator: JwtAuthenticator,
    offerService: OfferService,
    offerRepository: OfferRepository)(implicit ec: ExecutionContext)
    extends EndpointWithAuthentication(jwtAuthenticator) {

  override protected implicit val formats
    : Formats = DefaultFormats ++ CurrencySerializers.currencyFormats

  private val allOffersRoute =
    (path("offers") & get & authenticate) { _ =>
      onSuccess(offerRepository.getAllOffers) { offers =>
        complete(Statuses.OK -> offers)
      }
    }

  private val singleOfferRoute =
    (path("offer" / Segment) & get & authenticate) { (offerId, _) =>
      onSuccess(offerRepository.getOfferWithId(offerId)) {
        case Some(offer) => complete(Statuses.OK -> offer)
        case None        => complete(Statuses.NotFound)
      }
    }

  private val offerAddRoute =
    (path("offer") & post & entity(as[OfferCreationData]) & authenticate) {
      (offerCreationData, userId) =>
        onSuccess(offerService.createOffer(userId, offerCreationData)) {
          case Left(error) => complete(errorsResponse(List(errorToJson(error))))
          case Right(id)   => complete(idResponse("offerId", id))
        }
    }

  private val offerExchangeRoute =
    (path("offer" / Segment / "exchange") & post & authenticate) {
      (offerId, userId) =>
        onSuccess(offerService.exchangeWithOffer(userId, offerId)) {
          case Left(error) => complete(errorsResponse(List(errorToJson(error))))
          case Right(_)    => complete(emptyOkResponse)
        }
    }

  override def routes: Seq[Route] =
    offerAddRoute :: offerExchangeRoute :: singleOfferRoute :: allOffersRoute :: Nil
}
