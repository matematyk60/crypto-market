package com.market.e2e.api.jwt

import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtAsymetricAlgorithm

trait JwtCommons {

  protected implicit val formats: DefaultFormats.type = DefaultFormats
  protected implicit val serialization: Serialization.type = Serialization

  protected val algorithm: JwtAsymetricAlgorithm = JwtAlgorithm.RS256

  protected val marketIssuer = "crypto"
  protected val marketAudience = "crypto"

}
