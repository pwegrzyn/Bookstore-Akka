package bookstore.client

import akka.actor.{ActorSystem, Props}
import bookstore.model.{FindBook, OrderBook, StreamBookContent}
import com.typesafe.config.ConfigFactory

object Client {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load("client");
    val system = ActorSystem("client", config)
    val actorRef = system.actorOf(Props[ClientSupervisorActor], "client")

    println("Init done. Please type in your commands:")
    var loopRunning = true
    while (loopRunning) {
      val input = scala.io.StdIn.readLine().split(" ").toList
      input match {
        case "search" :: title :: _ =>
          actorRef ! FindBook(title)
        case "order" :: title :: _ =>
          actorRef ! OrderBook(title)
        case "stream" :: title :: _ =>
          actorRef ! StreamBookContent(title)
        case "quit" :: _ =>
          loopRunning = false
        case _ =>
          println("Invalid command. Please try again...")
      }
    }

    system.terminate()
  }

}
