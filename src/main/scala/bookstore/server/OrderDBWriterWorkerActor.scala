package bookstore.server

import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging

class OrderDBWriterWorkerActor extends Actor with ActorLogging{

  val orderDbPath = "database/orders.txt"
  var fileWriter: FileWriter = _

  override val log = Logging(context.system, this)

  override def preStart: Unit = {
    fileWriter = new FileWriter(orderDbPath, true)
  }

  override def receive: Receive = {
    case title: String => {
      log.info("Saving the order to the database...")
      val timestampStr = LocalDateTime.now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))
      fileWriter.write("Order {title: " + title + ", timestamp: " + timestampStr + "}\n")
      fileWriter.flush();
    }
    case _ =>
      log.info("Order DB Writer Worker received an unknown message type!")
  }

  override def postStop(): Unit = {
    fileWriter.close()
  }

}
