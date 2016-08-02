package edu.eckerd.google.files.uploader

import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY}

import akka.actor.{ActorRef, ActorSystem, Props}
import better.files.FileWatcher
import better.files.FileWatcher.Callback
import better.files.FileWatcher.Event
import better.files.FileWatcher.Message
import edu.eckerd.google.api.services.drive.Drive
import edu.eckerd.google.api.services.Scopes.DRIVE
import edu.eckerd.google.api.services.drive.models.{File => gFile}
import edu.eckerd.google.api.services.drive.models.{FileContent => gFileContent}
import eri.commons.config.SSConfig

/**
  * Created by davenpcm on 8/2/16.
  */
object MainApplication extends App{
  val conf = new SSConfig()
  implicit val system = ActorSystem("mySystem")
  implicit val drive = Drive(
    conf.google.serviceAccount.as[String],
    conf.google.impersonatedAccount.as[String],
    //its-drive_owner@eckerd.edu,
    conf.google.credentialFilePath.as[String],
    conf.google.applicationName.as[String],
    DRIVE
  )

  val watcher: ActorRef = system.actorOf(
    Props(
      new FileWatcher(
        Paths.get(
          conf.watcher.location.as[String]
        )
      )
    )
  )

  def when(events: Event*)(callback: Callback): Message = {
    Message.RegisterCallback(events.distinct, callback)
  }

  watcher ! when(events = ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE){
    case (ENTRY_CREATE, file) =>
      println(s"$file got created")

      val myFile = gFile(
        file.name,
        "text/plain",
        None,
        None,
        None,
        Some(
          List(conf.watcher.googleID.as[String])
        ),
        Some(gFileContent( file.path.toAbsolutePath.toString , "text/plain"))
      )
      drive.files.create(myFile)


    case (ENTRY_MODIFY, file) => println(s"$file got modified")
    case (ENTRY_DELETE, file) => println(s"$file got deleted")
  }

}
