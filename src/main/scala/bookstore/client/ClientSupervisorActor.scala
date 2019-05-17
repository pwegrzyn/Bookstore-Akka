package bookstore.client

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import akka.event.Logging
import bookstore.model.BookstoreRequest

import scala.concurrent.duration._

class ClientSupervisorActor extends Actor with ActorLogging{

  val workerName = "client_worker"
  override val log = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _ => Restart
  }

  override def preStart: Unit = {
    context.actorOf(Props[ClientWorkerActor], workerName)
  }

  override def receive: Receive = {
    case cmd: BookstoreRequest => context.child(workerName).get ! cmd
    case _                     => log.info("Client Supervisor received unknown message type!")
  }

}
