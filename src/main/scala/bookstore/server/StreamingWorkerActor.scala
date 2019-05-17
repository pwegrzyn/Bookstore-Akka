package bookstore.server

import java.nio.file.Paths

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.event.Logging
import akka.stream.{ActorMaterializer, IOResult, ThrottleMode}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import bookstore.model.{Book, BookSearchResult, FindBook, StreamBookContent, StreamCompleted, StreamContent, StreamException}

import scala.concurrent.Future
import scala.concurrent.duration._

class StreamingWorkerActor extends Actor with ActorLogging{

  var client: ActorRef = _
  val supervisorPath = "akka.tcp://server@127.0.0.1:3552/user/server"

  override val log = Logging(context.system, this)

  implicit val actorSystem: ActorSystem = context.system
  import actorSystem.dispatcher
  implicit val flowMaterializer: ActorMaterializer = ActorMaterializer()
  override def receive: Receive = {
    case request: (StreamBookContent, ActorRef) => {
      client = request._2
      log.info("Asking the Supervisor if the book: " + request._1.bookTitle + " exists in the databases...")
      context.actorSelection(supervisorPath) ! FindBook(request._1.bookTitle)
    }
    case BookSearchResult(foundBook) => {
      foundBook match {
        case Some(Book(title, _)) => {
          log.info("The book: " + title + " exists in one of the databases.")
          log.info("Beginning the process of streaming the content of the book...")

          val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get("gutenberg/" + title + ".txt"))
          val flow = Flow[ByteString]
            .via(Framing.delimiter(ByteString(System.lineSeparator), 256))
            .throttle(1, 1.second, 1, ThrottleMode.shaping)
          val sink: Sink[ByteString, Future[Done]] = Sink.foreach(client ! StreamContent(_))
          source.via(flow).runWith(sink).onComplete(_ => {
            log.info("Streaming done...")
            client ! StreamCompleted()
            context.stop(self)
          })
        }
        case None => {
          log.info("The book does not exist in any of the databases. Informing the client about the failure...")
          client ! StreamException(new Exception("This book does not exist in the database."))
          context.stop(self)
        }
      }
    }
    case _ =>
      log.info("Streaming Worker received an unknown message type!")
  }

}
