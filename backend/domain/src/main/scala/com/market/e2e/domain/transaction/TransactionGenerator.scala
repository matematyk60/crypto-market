package com.market.e2e.domain.transaction

import com.market.e2e.domain.offer.ExchangeOffer

object TransactionGenerator {

  def generateTransactions(firstOffer: ExchangeOffer,
                           secondOffer: ExchangeOffer): List[Transaction] =
    (firstOffer, secondOffer) match {
      case (ExchangeOffer(_,
                          fUserId,
                          _,
                          fSellingCurrency,
                          fBuyingCurrency,
                          fAmount,
                          fMinimalRateSellingToBuying,
                          _,
                          _),
            ExchangeOffer(_,
                          sUserId,
                          _,
                          sSellingCurrency,
                          sBuyingCurrency,
                          sAmount,
                          sMinimalRateSellingToBuying,
                          _,
                          _)) =>
        val fExchangeRatio = (fMinimalRateSellingToBuying + (1 / sMinimalRateSellingToBuying)) / 2
        val sEchangeRatio = 1 / fExchangeRatio
        val fSelled = math.min(sEchangeRatio * sAmount, fAmount)
        val sSelled = math.min(fExchangeRatio * fAmount, sAmount)

        val systemTransactions = List(Transaction(0,
                                                  TransactionTypes.Debit,
                                                  fSellingCurrency,
                                                  fSelled * 0.05),
                                      Transaction(0,
                                                  TransactionTypes.Debit,
                                                  sSellingCurrency,
                                                  sSelled * 0.05))
        val fReallySelled = fSelled * 0.95
        val sReallySelled = sSelled * 0.95

        val fTransactions = List(
          Transaction(fUserId,
                      TransactionTypes.Credit,
                      fSellingCurrency,
                      fSelled),
          Transaction(fUserId,
                      TransactionTypes.Debit,
                      sSellingCurrency,
                      sReallySelled)
        )
        val sTransactions = List(
          Transaction(sUserId,
                      TransactionTypes.Credit,
                      sSellingCurrency,
                      sSelled),
          Transaction(sUserId,
                      TransactionTypes.Debit,
                      fSellingCurrency,
                      fReallySelled)
        )

        systemTransactions ++ fTransactions ++ sTransactions
    }
}
