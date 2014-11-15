name := "spring-security-play2-parent"

Common.settings

libraryDependencies ++= Seq()

lazy val module = (project in file("./modules/spring-security-play2"))
  .enablePlugins(PlayScala)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    javacOptions ++= Seq("-source", "1.8")
  )
  .dependsOn(module)
  .aggregate(module)

libraryDependencies ++= Seq(
  javaWs,
  "org.springframework" % "spring-framework-bom" % Common.springVersion
//  "org.springframework" % "spring-core" % Common.springVersion,
//  "org.springframework" % "spring-beans" % Common.springVersion,
//  "org.springframework" % "spring-context" % Common.springVersion,
//  "org.springframework" % "spring-aop" % Common.springVersion,
//  "org.springframework" % "spring-aspects" % Common.springVersion
)

releaseSettings

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
