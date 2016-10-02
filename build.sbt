/**
  *  Copyright 2016 Yuriy Yarosh
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *  http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  **/
import sbt.Keys._

val versions = Map(
  "scala"     -> "2.11.8",
  "jackson"   -> "2.8.2",
  "scalatest" -> "3.0.0",
  "scalaxml"  -> "1.0.5"
)

lazy val commonSettings = Seq(
  organization := "bitsnap",
  scalaVersion := versions("scala")
  // libraryDependencies += "org.scala-lang" % "scala-reflect" % versions("scala")
)

lazy val pathSettings = Seq(
  crossPaths := false,
  javaSource in Compile := baseDirectory.value / "src",
  javaSource in Test := baseDirectory.value / "test",
  scalaSource in Compile := baseDirectory.value / "src",
  scalaSource in Test := baseDirectory.value / "test",
  resourceDirectory in Compile := baseDirectory.value / "resources",
  resourceDirectory in Test := baseDirectory.value / "resources",
  watchSources ++= Seq(
    baseDirectory.value / "src",
    baseDirectory.value / "test",
    baseDirectory.value / "resources"
  )
)

lazy val dependencies = Seq(
  libraryDependencies ++= Seq(
    "com.fasterxml.jackson.core" % "jackson-core"        % versions("jackson"),
    "com.fasterxml.jackson.core" % "jackson-databind"    % versions("jackson"),
    "com.fasterxml.jackson.core" % "jackson-annotations" % versions("jackson"),
    "org.scalatest"              % "scalatest_2.11"      % versions("scalatest") % "test",
    "org.scala-lang.modules"     % "scala-xml_2.11"      % versions("scalaxml") % "test"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(pathSettings: _*)
  .settings(dependencies: _*)
  .settings(
    name := "bitsnap-framework",
    version := "0.0.1",
    maxErrors := 20,
    pollInterval := 1000,
    scalacOptions ++= Seq("-deprecation", "-Xcheckinit", "-unchecked", "-feature"),
    javaOptions += "-Xmx1G",
    fork := true,
    fork in Test := true,
    parallelExecution := true,
    parallelExecution in Test := true,
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    scalafmtConfig in ThisBuild := Some(file(".scalafmt"))
  )
