package com.market.e2e.specs

import com.typesafe.config.{Config, ConfigValueFactory}

trait AuthenticationSpec {

  protected val publicKeyFile: String =
    classOf[AuthenticationSpec].getClassLoader.getResource("public.der").getPath
  protected val privateKeyFile: String =
    classOf[AuthenticationSpec].getClassLoader
      .getResource("private.der")
      .getPath

  implicit class WithAuthenticationConfig(config: Config) {

    def withAuthenticationConfig =
      config
        .withValue("authentication.private-key-file",
                   ConfigValueFactory.fromAnyRef(privateKeyFile))
        .withValue("authentication.public-key-file",
                   ConfigValueFactory.fromAnyRef(publicKeyFile))
  }

}
