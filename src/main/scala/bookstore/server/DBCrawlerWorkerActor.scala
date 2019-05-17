package bookstore.server

import java.io.File

import akka.actor.{Actor, ActorLogging}
import akka.event.Logging
import bookstore.model.{Book, BookSearchResult, FindBook}
import com.github.tototoshi.csv._

class DBCrawlerWorkerActor extends Actor with ActorLogging{

  override val log = Logging(context.system, this)

  override def receive: Receive = {
    case request: (FindBook, String) => {
      log.info("DBCrawler unit starting to search the database " + request._2 + "...")
      val foundBook = findBook(request._1.bookTitle, request._2)
      sender ! BookSearchResult(foundBook)
    }
    case _ =>
      log.info("DB Crawler received an unknown message type!")
  }

  def findBook(title: String, dbPath: String): Option[Book] = {
    val reader = CSVReader.open(new File(dbPath))
    val foundRecord = reader.iterator.find(fields => fields(0) == title)
    val foundBook: Option[Book] = foundRecord match {
      case Some(fields) => {
        log.info("DB Crawler unit found the book: " + title + " in database: " + dbPath)
        Some(Book(fields(0), fields(1).toLong))
      }
      case None => {
        log.info("DB Crawler unit have not found the book: " + title + " in database: " + dbPath)
        None
      }
    }
    reader.close()
    foundBook
  }

}
