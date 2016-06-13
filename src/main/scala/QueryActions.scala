import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.trace.Pos

// Demonstrates various ways of reading data
object QueryActions extends App {

  // A simple dictionary table with keys and values
  class Dict(tag: Tag) extends Table[(Int, String)](tag, "INT_DICT") {
    def key = column[Int]("KEY", O.PrimaryKey)
    def value = column[String]("VALUE")
    def * = (key, value)
  }
  val dict = TableQuery[Dict]

  val db = Database.forConfig("h2mem1")
  try {

    // Define a pre-compiled parameterized query for reading all key/value
    // pairs up to a given key.
    // val upTo = Compiled { k: Column[Int] =>
    val upTo = Compiled { k: Rep[Int] =>
      dict.filter(_.key <= k).sortBy(_.key)
    }

    // A second pre-compiled query which returns a Set[String]
    val upToSet = upTo.map(_.andThen(_.to[Set]))

    Await.result(db.run(DBIO.seq(

      // Create the dictionary table and insert some data
      // dict.ddl.create,
      dict.schema.create,
      dict ++= Seq(1 -> "a", 2 -> "b", 3 -> "c", 4 -> "d", 5 -> "e"),

      upTo(3).result.map { r =>
        println( "Seq (Vector) of k/v pairs up to 3" + Pos() )
        println("- " + r + Pos() )
      },

      upToSet(3).result.map { r =>
        println("Set of k/v pairs up to 3" + Pos() )
        println("- " + r + Pos() )
      },

      dict.map(_.key).to[Array].result.map { r =>
        println("All keys in an unboxed Array[Int]" + Pos() )
        println("- " + r + Pos() )
      },

      upTo(3).result.head.map { r =>
        println("Only get the first result, failing if there is none" + Pos() )
        println("- " + r + Pos() )
      },

      upTo(3).result.headOption.map { r =>
        println("Get the first result as an Option, or None" + Pos() )
        println("- " + r + Pos() )
      }

    )), Duration.Inf)

    // The Publisher captures a Database plus a DBIO action.
    // The action does not run until you consume the stream.
    val p = db.stream(upTo(3).result)

    println("Stream k/v pairs up to 3 via Reactive Streams" + Pos() )
    Await.result(p.foreach { v =>
      println("- " + v + Pos() )
    }, Duration.Inf)

  } finally db.close
}
