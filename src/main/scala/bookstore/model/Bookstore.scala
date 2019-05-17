package bookstore.model

import akka.util.ByteString

sealed trait BookstoreAction extends Serializable

sealed trait BookstoreRequest extends BookstoreAction

sealed trait BookstoreResponse extends BookstoreAction

case class Book(title: String, price: Long)

case class FindBook(bookTitle: String) extends BookstoreRequest

case class OrderBook(bookTitle: String) extends BookstoreRequest

case class StreamBookContent(bookTitle: String) extends BookstoreRequest

case class BookSearchResult(foundBook: Option[Book]) extends BookstoreResponse

case class OrderCompleted(info: String) extends BookstoreResponse

case class StreamContent(content: ByteString) extends BookstoreResponse

case class StreamCompleted() extends BookstoreResponse

case class StreamException(ex: Throwable) extends BookstoreResponse