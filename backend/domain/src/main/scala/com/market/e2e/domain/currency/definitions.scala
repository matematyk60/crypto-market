package com.market.e2e.domain.currency

sealed trait Currency

object Currencys {
  case object BitCoin extends Currency
  case object Litecoin extends Currency
}
