package com.market.e2e.infrastructure.mongo

import MongoUserRepository.UserDTO
import com.market.e2e.domain.TypeAliases.UserId
import com.market.e2e.domain.user.{User, UserDetails, UserRepository}
import hero.mongo.MongoRepository
import reactivemongo.api.DefaultDB
import reactivemongo.bson.BSONString

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class MongoUserRepository(db: DefaultDB)(implicit ec: ExecutionContext)
    extends MongoRepository(db, "userRepository")
    with UserRepository {

  import com.market.e2e.infrastructure.mongo.wr.UserWRs._

  override def saveNewUser(userDetails: UserDetails,
                           id: Option[Int] = None): Future[UserId] = {
    val userId = id.getOrElse(Random.nextInt(10000))
    save(UserDTO(userId, userDetails.email, userDetails.password)).map(_ =>
      userId)
  }

  override def getUser(email: String): Future[Option[User]] =
    getBy("email", BSONString(email)).map(_.map(_.toDomain))
}

object MongoUserRepository {
  final case class UserDTO(
      id: Int,
      email: String,
      password: String
  ) {
    def toDomain = User(id, UserDetails(email, password))
  }
}
