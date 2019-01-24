package com.market.e2e.api.user

import akka.http.scaladsl.server.Route
import com.market.e2e.api.CurrencySerializers
import com.market.e2e.api.core.EndpointWithAuthentication
import com.market.e2e.api.jwt.JwtAuthenticator
import com.market.e2e.api.user.UserJsons.AddFundsJson
import com.market.e2e.domain.funds.FundsService
import org.json4s.{DefaultFormats, Formats}

import scala.concurrent.ExecutionContext

class UserEndpoint(jwtAuthenticator: JwtAuthenticator,
                   fundsService: FundsService)(implicit ec: ExecutionContext)
    extends EndpointWithAuthentication(jwtAuthenticator) {

  override protected implicit val formats
    : Formats = DefaultFormats ++ CurrencySerializers.currencyFormats

  private val myFunds = (path("user" / "me" / "funds") & get & authenticate) {
    userId =>
      onSuccess(fundsService.usersAccountBalance(userId)) { funds =>
        complete(Statuses.OK -> funds)
      }
  }

  private val myCurrencyFunds =
    (path("user" / "me" / "funds" / "currency" / Segment) & get & authenticate) {
      (currencyString, userId) =>
        CurrencySerializers.currencyFromString(currencyString) match {
          case Some(currency) =>
            onSuccess(
              fundsService.usersAccountBalanceInCurrency(userId, currency)) {
              funds =>
                complete(Statuses.OK -> funds)
            }
          case None =>
            complete(Statuses.BadRequest)
        }
    }

  private val addFunds =
    (path("user" / "me" / "funds") & post & entity(as[AddFundsJson]) & authenticate) {
      (request, userId) =>
        onSuccess(
          fundsService
            .addFundsToUserAccount(userId, request.currency, request.amount))(
          complete(emptyOkResponse))
    }

  override def routes: Seq[Route] =
    myFunds :: addFunds :: myCurrencyFunds :: Nil
}
