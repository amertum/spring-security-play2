name := "spring-security-play2"

Common.settings

libraryDependencies ++= Seq(
  "org.springframework.security" % "spring-security-core" % Common.springSecurityVersion,
  "org.springframework.security" % "spring-security-crypto" % Common.springSecurityVersion,
  "org.springframework.security" % "spring-security-config" % Common.springSecurityVersion
)
