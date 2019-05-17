package bookstore.server

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Server {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("server");
    val system = ActorSystem("server", config)
    system.actorOf(Props[ServerSupervisorActor], "server")

    println("Listening for clients... Type 'quit' to terminate the server system.")

    var loopRunning = true
    while (loopRunning) {
      val input = scala.io.StdIn.readLine().split(" ").toList
      input match {
        case "quit" :: _ =>
          loopRunning = false
        case _ =>
          println("Invalid command. Please try again...")
      }
    }

    system.terminate()
  }

}
