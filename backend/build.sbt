name := "crypto-market-backend"
version := "0.01"
scalaVersion := "2.12.7"

enablePlugins(SbtNativePackager)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerBaseImage := "java:openjdk-8"
daemonUser := "root"
dockerRepository := Some("matematyk60")
dockerExposedPorts := Seq(8080)

val commonSettings = Seq(
  scalaVersion := "2.12.6",
  resolvers ++= Dependencies.additionalResolvers,
  excludeDependencies ++= Dependencies.globalExcludes,
  scalacOptions ++= CompilerOpts.scalacOptions,
  publishArtifact in (Compile, packageDoc) := false,
  publishArtifact in packageDoc := false,
  sources in (Compile, doc) := Seq.empty,
  scalafmtTestOnCompile in ThisBuild := true,
  scalafmtOnCompile in ThisBuild := true,
  parallelExecution in Test := false
)

lazy val `api` =
  project
    .in(file("api"))
    .settings(commonSettings)
    .settings(libraryDependencies ++= Dependencies.apiDependencies)
    .dependsOn(`domain`, `shared`)

lazy val `domain` =
  project
    .in(file("domain"))
    .settings(commonSettings)
    .settings(libraryDependencies ++= Dependencies.domainDependencies)
    .dependsOn(`shared`)

lazy val `infrastructure` =
  project
    .in(file("infrastructure"))
    .settings(commonSettings)
    .settings(libraryDependencies ++= Dependencies.infrastructureDependencies)
    .dependsOn(`domain`, `shared`)

lazy val `shared` =
  project
    .in(file("shared"))
    .settings(commonSettings)
    .settings(libraryDependencies ++= Dependencies.sharedDependencies)

lazy val `market-backend` =
  project
    .in(file("."))
    .settings(commonSettings)
    .settings(libraryDependencies ++= Dependencies.rootDependencies)
    .settings(
      dockerBaseImage := "java:openjdk-8",
      daemonUser in Docker := "root",
      dockerExposedPorts := Seq(8080)
    )
    .aggregate(`api`, `domain`, `infrastructure`, `shared`)
    .settings(aggregate in Docker := false)
    .dependsOn(`api`, `domain`, `infrastructure`, `shared`)
