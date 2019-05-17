package bookstore.server

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.event.Logging
import bookstore.model.{Book, BookSearchResult, FindBook}
import java.io.{FileNotFoundException, IOException}

import scala.concurrent.duration._

class SearchWorkerActor extends Actor with ActorLogging{

  val db1Path = "database/db1.csv"
  val db2Path = "database/db2.csv"
  var client: ActorRef = _
  var firstCrawlerFailed = false

  override val log = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: FileNotFoundException => Escalate
    case _: IOException => Escalate
    case _ => Restart
  }

  override def postStop(): Unit = {
    context.children.foreach(context.stop)
  }

  override def receive: Receive = {
    case request: (FindBook, ActorRef) => {
      log.info("Delegating DBCrawlers to search for the book in the DBs...")
      client = request._2
      context.actorOf(Props[DBCrawlerWorkerActor]) ! (request._1, db1Path)
      context.actorOf(Props[DBCrawlerWorkerActor]) ! (request._1, db2Path)
    }
    case BookSearchResult(foundBook) =>
      foundBook match {
        case result: Some[Book] => {
          log.info("DBCrawler found the desired book.")
          client ! BookSearchResult(result)
          context.stop(self)
        }
        case None if !firstCrawlerFailed => {
          firstCrawlerFailed = true
          context.stop(sender)
        }
        case None if firstCrawlerFailed => {
          log.info("DBCrawlers have not found the desired book.")
          client ! BookSearchResult(None)
          context.stop(self)
        }
      }
    case _ =>
      log.info("Search Worker received an unknown message type!")
  }

}
