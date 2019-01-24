package com.market.e2e.api.open

final case class UserRegistrationJson(email: String, password: String)

final case class UserLoginJson(email: String, password: String)

final case class AuthResponse(token: String, userId: Int)
