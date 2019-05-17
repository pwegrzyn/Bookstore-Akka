package bookstore.server

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.event.Logging
import bookstore.model.{StreamBookContent}

import scala.concurrent.duration._

class StreamingSupervisorActor extends Actor with ActorLogging{

  override val log = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _ => Restart
  }

  override def receive: Receive = {
    case request: (StreamBookContent, ActorRef) => {
      log.info("Forwarding Request to Streaming Worker")
      context.actorOf(Props[StreamingWorkerActor]) ! request
    }
    case _ =>
      log.info("Streaming Supervisor received an unknown message type!")
  }

}
