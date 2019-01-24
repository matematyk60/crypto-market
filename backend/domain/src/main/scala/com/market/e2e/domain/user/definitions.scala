package com.market.e2e.domain.user

import com.market.e2e.domain.TypeAliases.UserId

final case class User(id: UserId, details: UserDetails)

final case class UserDetails(email: String, password: String)
