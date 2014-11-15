import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._

object Common {
  val settings: Seq[Setting[_]] = releaseSettings ++ Seq(
    organization := "com.github.amertum",
    scalaVersion := "2.11.1",
    doc in Compile <<= target.map(_ / "none") // do not package documentation (sources, javadocs)
  )

  val springVersion = "4.1.2.RELEASE"
  val springSecurityVersion = "3.2.5.RELEASE"
  val aspectjVersion = "1.8.3"

  val dependencies = Seq(
  )

}
