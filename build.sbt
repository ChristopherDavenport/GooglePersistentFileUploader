name := "GooglePersistentFileUploader"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "ERI OSS" at "http://dl.bintray.com/elderresearch/OSS"

libraryDependencies ++= {
  val betterFilesV = "2.16.0"
  Seq(
    "edu.eckerd" %% "google-api-scala" % "0.1.1-SNAPSHOT",
    "com.elderresearch" %% "ssc" % "0.2.0",
    "com.github.pathikrit" %% "better-files" % betterFilesV,
    "com.github.pathikrit" %% "better-files-akka" % betterFilesV,
    "com.typesafe.akka" %% "akka-actor" % "2.3.15"
  )
}
    