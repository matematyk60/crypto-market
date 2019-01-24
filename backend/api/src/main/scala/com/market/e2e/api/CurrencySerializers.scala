package com.market.e2e.api

import com.market.e2e.domain.currency.Currency
import com.market.e2e.domain.currency.Currencys.{BitCoin, Litecoin}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

object CurrencySerializers {

  case object CurrencySerializer
      extends CustomSerializer[Currency](_ =>
        ({
          case JString("BitCoin")  => BitCoin
          case JString("Litecoin") => Litecoin
        }, {
          case BitCoin  => JString("BitCoin")
          case Litecoin => JString("Litecoin")
        }))

  val currencyFormats = CurrencySerializer :: Nil

  def currencyFromString(currencyString: String): Option[Currency] =
    currencyString match {
      case "BitCoin"  => Some(BitCoin)
      case "Litecoin" => Some(Litecoin)
      case _          => None
    }
}
