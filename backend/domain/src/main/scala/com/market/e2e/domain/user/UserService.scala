package com.market.e2e.domain.user

import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.transaction.TransactionRepository

import scala.concurrent.{ExecutionContext, Future}

class UserService(userRepository: UserRepository,
                  transactionRepository: TransactionRepository)(
    implicit ec: ExecutionContext) {

  def logInUser(email: String, password: String): Future[Option[UserId]] =
    userRepository
      .getUser(email)
      .map(
        maybeUser =>
          maybeUser.flatMap(user =>
            if (user.details.password == password) Some(user.id)
            else None))

}
