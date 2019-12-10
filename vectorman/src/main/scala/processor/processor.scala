import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ListBuffer

package object processor {
  type Register = Int
  type ProgramCounter = Register
  val logger = Logger("root")
  val PC: Register = -1
  val MEM: Register = 1000

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

  implicit class ListBufferImprovements[A](l: ListBuffer[A]) {
    def popOrNone(): Option[A] ={
      if(l.nonEmpty){
        Some(l.remove(0))
      }
      else None
    }
  }

}
