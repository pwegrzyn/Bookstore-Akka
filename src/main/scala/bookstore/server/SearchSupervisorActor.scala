package bookstore.server

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.event.Logging
import bookstore.model.FindBook

import scala.concurrent.duration._

class SearchSupervisorActor extends Actor with ActorLogging{

  override val log = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _ => Restart
  }

  override def receive: Receive = {
    case request: (FindBook, ActorRef) => {
      log.info("Forwarding Request to Search Worker")
      context.actorOf(Props[SearchWorkerActor]) ! request
    }
    case _ =>
      log.info("Search Supervisor received an unknown message type!")
  }

}
