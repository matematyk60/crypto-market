package com.market.e2e.domain.user

import com.market.e2e.domain.TypeAliases.UserId

import scala.concurrent.Future

trait UserRepository {
  def saveNewUser(userDetails: UserDetails,
                  id: Option[Int] = None): Future[UserId]

  def getUser(email: String): Future[Option[User]]
}
