package bookstore.server

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import akka.event.Logging
import bookstore.model.{FindBook, OrderBook, StreamBookContent}

import scala.concurrent.duration._

class ServerSupervisorActor extends Actor with ActorLogging{

  val searchSupervisor = "search_supervisor"
  val orderSupervisor = "order_supervisor"
  val streamingSupervisor = "stream_supervisor"

  override val log = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _ => Restart
  }

  override def preStart: Unit = {
    context.actorOf(Props[SearchSupervisorActor], searchSupervisor)
    context.actorOf(Props[OrderSupervisorActor], orderSupervisor)
    context.actorOf(Props[StreamingSupervisorActor], streamingSupervisor)
  }

  override def receive: Receive = {
    case request: FindBook => {
      log.info("Server received Search Task - delegating to Search Supervisor...")
      context.child(searchSupervisor).get ! (request, sender)
    }
    case request: OrderBook => {
      log.info("Server received Order Task - delegating to Order Supervisor...")
      context.child(orderSupervisor).get ! (request, sender)
    }
    case request: StreamBookContent => {
      log.info("Server received Content Stream Task - delegating to Stream Supervisor...")
      context.child(streamingSupervisor).get ! (request, sender)
    }
    case _ =>
      log.info("Server Supervisor received an unknown message type!")
  }

}
