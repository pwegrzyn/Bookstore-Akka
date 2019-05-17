package bookstore.client

import akka.actor.{Actor, ActorLogging}
import akka.event.Logging
import bookstore.model.{Book, BookSearchResult, BookstoreRequest, BookstoreResponse, OrderCompleted, StreamCompleted, StreamContent, StreamException}

class ClientWorkerActor extends Actor with ActorLogging{

  val serverPath = "akka.tcp://server@127.0.0.1:2552/user/"
  override val log = Logging(context.system, this)

  override def receive: Receive = {
    case request: BookstoreRequest =>
      context.actorSelection(serverPath + "bookstore") ! request
    case response: BookstoreResponse =>
      response match {
        case BookSearchResult(foundBook) =>
          foundBook match {
            case Some(Book(title, price)) =>
              println("Found book - title: " + title + ", price: " + price / 100)
            case None =>
              println("No such book on the warehouse!")
          }
        case OrderCompleted(info) =>
          println("The order has been completed. Info: " + info)
        case StreamContent(line) =>
          println("Received stream line: " + line)
        case StreamCompleted() =>
          println("Successfully completed the streaming of the book content.")
        case StreamException(ex) =>
          println("An error has occurred while streaming the book content. Reason: " + ex)
      }
    case _ =>
      log.info("Client Worker received unknown message type!")
  }

}
