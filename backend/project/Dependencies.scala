import DependencyVersions._
import sbt.{Resolver, SbtExclusionRule, _}

object Dependencies {
  private val loggingDependencies = Seq(
    "ch.qos.logback"             % "logback-classic"  % logbackVersion,
    "ch.qos.logback"             % "logback-core"     % logbackVersion,
    "org.slf4j"                  % "jcl-over-slf4j"   % slf4jVersion,
    "org.slf4j"                  % "log4j-over-slf4j" % slf4jVersion,
    "com.typesafe.scala-logging" %% "scala-logging"   % scalaLoggingVersion
  )

  private val utilDependencies = Seq(
    "com.roundeights" %% "hasher"            % hasherVersion,
    "com.pauldijou"   %% "jwt-json4s-native" % jwtVersion
  )

  private val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor"  % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"  % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion
  )

  private val akkaHttpDependencies = Seq(
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core"       % akkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-json4s"     % akkaJson4sVersion,
    "ch.megard"         %% "akka-http-cors"       % corsVersion,
    "org.json4s"        %% "json4s-native"        % json4sVersion,
    "org.json4s"        %% "json4s-core"          % json4sVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % sprayJsonVersion
  )

  private val mongoDependencies = Seq(
    "org.reactivemongo" %% "reactivemongo"            % mongoVersion,
    "org.reactivemongo" %% "reactivemongo-akkastream" % mongoVersion
  )

  private val testDependencies = Seq(
    "org.scalactic"           %% "scalactic"          % scalaTestVersion        % Test,
    "org.scalatest"           %% "scalatest"          % scalaTestVersion        % Test,
    "com.typesafe.akka"       %% "akka-http-testkit"  % akkaHttpVersion         % Test
  )

  private val commonDependencies = Seq(
    "io.codeheroes" %% "codeheroes-commons" % codeheroesCommonsVersion
  )

  private val functionalDependencies = Seq(
    "org.typelevel"              %% "cats-core"     % catsVersion,
    "org.typelevel"              %% "mouse"         % mouseVersion
  )

  val domainDependencies: Seq[ModuleID] = Seq(
    utilDependencies,
    akkaDependencies,
    functionalDependencies,
    loggingDependencies
  ).reduce(_ ++ _)

  val apiDependencies: Seq[ModuleID] = Seq(
    akkaHttpDependencies
  ).reduce(_ ++ _)

  val infrastructureDependencies: Seq[ModuleID] = Seq(
    akkaHttpDependencies,
    mongoDependencies,
    commonDependencies
  ).reduce(_ ++ _)

  val sharedDependencies: Seq[ModuleID] =
    Seq(
      functionalDependencies
    ).reduce(_ ++ _)

  val rootDependencies: Seq[ModuleID] = Seq(
    testDependencies
  ).reduce(_ ++ _)

  val additionalResolvers = Seq(
    "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven",
    "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.typesafeIvyRepo("releases"),
    "Sonatype Repository" at "https://oss.sonatype.org/content/groups/public",
    Resolver.bintrayRepo("codeheroes", "maven")
  )

  val globalExcludes = Seq(
    SbtExclusionRule("log4j"),
    SbtExclusionRule("log4j2"),
    SbtExclusionRule("commons-logging")
  )
}
