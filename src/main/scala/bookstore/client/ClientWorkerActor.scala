package bookstore.client

import akka.actor.{Actor, ActorLogging}
import akka.event.Logging
import bookstore.client.ClientSupervisorActor.Command

class ClientWorkerActor extends Actor with ActorLogging{

  val serverPath = "akka.tcp://server@127.0.0.1:2552/user/"
  override val log = Logging(context.system, this)

  override def receive: Receive = {
    case Command(cmd) => println(cmd)
    case _            => log.info("Client Worker received unknown message type!")
  }

}
