// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.1"

name := "vectorman"
organization := "com.lewiky"
version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

