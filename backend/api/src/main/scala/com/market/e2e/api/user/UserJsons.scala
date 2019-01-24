package com.market.e2e.api.user

import com.market.e2e.domain.currency.Currency

object UserJsons {

  final case class AddFundsJson(currency: Currency, amount: Double)
}
