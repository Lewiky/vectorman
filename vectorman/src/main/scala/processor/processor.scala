import com.typesafe.scalalogging.Logger

package object processor {
  type Register = Int
  type ProgramCounter = Register
  val logger = Logger("root")
  val PC: Register = -1

  implicit class ListImprovements[A](l: List[A]) {
    def generatePairs: IndexedSeq[(A, A)] = {
      for (i <- 0.until(l.length))
        yield (l(i), l(i + 1))
    }
  }

  implicit class OptionImprovements[A](o: Option[A]) {
    def someOr(a: A): A = {
      o match {
        case Some(x) => x
        case None => a
      }
    }
  }

}
