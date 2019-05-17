package bookstore.server

import java.io.{FileNotFoundException, IOException}

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.event.Logging
import bookstore.model.{OrderBook}

import scala.concurrent.duration._

class OrderSupervisorActor extends Actor with ActorLogging{

  val orderDBWriterWorkerActor = "orderDBWriterWorkerActor"

  override val log = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: FileNotFoundException => Escalate
    case _: IOException => Escalate
    case _ => Restart
  }

  override def preStart: Unit = {
    context.actorOf(Props[OrderDBWriterWorkerActor], orderDBWriterWorkerActor)
  }

  override def receive: Receive = {
    case request: (OrderBook, ActorRef) => {
      log.info("Forwarding Request to Order Worker")
      context.actorOf(Props[OrderWorkerActor]) ! request
    }
    case title: String => {
      context.child(orderDBWriterWorkerActor).get ! title
    }
    case _ =>
      log.info("Order Supervisor received an unknown message type!")
  }

}
