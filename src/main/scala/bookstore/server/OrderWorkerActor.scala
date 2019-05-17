package bookstore.server

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.event.Logging
import bookstore.model.{Book, BookSearchResult, FindBook, OrderBook, OrderCompleted}

class OrderWorkerActor extends Actor with ActorLogging{

  var client: ActorRef = _
  val supervisorPath = "akka.tcp://server@127.0.0.1:3552/user/server"
  val orderDbPath = "database/orders.txt"

  override val log = Logging(context.system, this)

  override def receive: Receive = {
    case request: (OrderBook, ActorRef) => {
      client = request._2
      log.info("Asking the Supervisor if the book: " + request._1.bookTitle + " exists in the databases...")
      context.actorSelection(supervisorPath) ! FindBook(request._1.bookTitle)
    }
    case BookSearchResult(foundBook) => {
      foundBook match {
        case Some(Book(title, _)) => {
          log.info("The book: " + title + " exists in one of the databases.")
          context.parent ! title
          log.info("Informing the client about the successfully completed order...")
          client ! OrderCompleted("Success: book has been ordered.")
          context.stop(self)

        }
        case None => {
          log.info("The book does not exist in any of the databases. Informing the client about the failure...")
          client ! OrderCompleted("Failure: this book does not exist in our database.")
          context.stop(self)
        }
      }
    }
    case _ =>
      log.info("Order Worker received an unknown message type!")
  }

}
