package bookstore.client

import akka.actor.{ActorSystem, Props}

import com.typesafe.config.ConfigFactory

object Client {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load("client.conf");
    val system = ActorSystem("client", config)
    val actorRef = system.actorOf(Props[ClientSupervisorActor], "client_supervisor")

    println("Init done. Please type in your commands:")
    var loopRunning = true
    while (loopRunning) {
      val line = scala.io.StdIn.readLine()
      line match {
        case "q" => loopRunning = false
        case _   => actorRef ! line
      }
    }

    system.terminate()
  }

}
