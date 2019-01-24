package com.market.e2e.api.open

import akka.http.scaladsl.server.Route
import com.market.e2e.api.core.Endpoint
import com.market.e2e.domain.auth.TokenProvider
import com.market.e2e.domain.user.{UserDetails, UserRepository, UserService}

class OpenEndpoint(userRepository: UserRepository,
                   userService: UserService,
                   tokenProvider: TokenProvider)
    extends Endpoint {

  private val userRegistrationRoute =
    (path("user" / "register") & post & entity(as[UserRegistrationJson])) {
      request =>
        onSuccess(
          userRepository.saveNewUser(
            UserDetails(request.email, request.password)))(userId =>
          onSuccess(tokenProvider.generateToken(userId)) { token =>
            complete(Statuses.OK -> AuthResponse(token, userId))
        })
    }

  private val userLogginRoute =
    (path("user" / "login") & post & entity(as[UserLoginJson])) { request =>
      onSuccess(userService.logInUser(request.email, request.password)) {
        case Some(userId) =>
          onSuccess(tokenProvider.generateToken(userId))(token =>
            complete(Statuses.OK -> AuthResponse(token, userId)))
        case None => complete(Statuses.Unauthorized)
      }
    }

  override def routes: Seq[Route] =
    userRegistrationRoute :: userLogginRoute :: Nil
}
