package bookstore.model

sealed trait BookstoreAction

sealed trait BookstoreRequest extends BookstoreAction

sealed trait BookstoreResponse extends BookstoreAction

case class Book(title: String, price: Double)

case class FindBook(bookTitle: String) extends BookstoreRequest

case class OrderBook(bookTitle: String) extends BookstoreRequest

case class StreamBookContent(bookTitle: String) extends BookstoreRequest

case class BookSearchResult(foundBook: Option[Book]) extends BookstoreResponse

case class OrderCompleted(info: String) extends BookstoreResponse

case class StreamContent(content: String) extends BookstoreResponse

case class StreamCompleted() extends BookstoreResponse

case class StreamException(ex: Throwable) extends BookstoreResponse