package processor.units

import processor._
import processor.units.circularBuffer.ReorderBufferEntry

import scala.collection.mutable.ListBuffer

class ReservationStation(executors: List[Executor]) extends EUnit[List[ReorderBufferEntry], List[ReorderBufferEntry]] {
  override var input: Option[List[ReorderBufferEntry]] = None
  override var output: Option[List[ReorderBufferEntry]] = None
  private var shelf: ListBuffer[(ReorderBufferEntry, Boolean)] = new ListBuffer[(ReorderBufferEntry, Boolean)]
  private def resolveDependencies(): Unit = {
    if (shelf.nonEmpty) {
      shelf(0) = (shelf(0)._1, true) //TODO: Actually
    }
  }

  def pipe(): Unit = {
    /*
    Pass all of the ready instructions forward to the ready execute units.
    Any instructions that don't have a ready unit to be dispatched to get put back on the shelf
     */
    var removed = new ListBuffer[ReorderBufferEntry]
    this.output match {
      case Some(output) => (executors.filter(_.isReady) zip output).foreach {
        case (executor: Executor, out: ReorderBufferEntry) => if (executor.input.isEmpty) {
          executor.input = Some(out)
          removed += out
        }
      }
        //Find which instructions couldn't be dispatched and reserve them again
        val notPiped: ListBuffer[ReorderBufferEntry] = (output diff removed).to(ListBuffer)
        this.shelf = this.shelf ++: notPiped.map(x => (x, true))
        this.output = None
      case None => ()
    }
  }

  def getNReadyInstructions(n: Int): List[ReorderBufferEntry] = {
    val buff = new ListBuffer[ReorderBufferEntry]
    for (_ <- 0 until n ) {
      this.getNextReadyInstruction match {
        case Some(x) => buff += x
        case None => ()
      }
    }
    buff.toList
  }

  def getNextReadyInstruction: Option[ReorderBufferEntry] = {
    this.resolveDependencies()
    for(item <- this.shelf){
      if(item._2){
        this.shelf -= item
        logger.debug(s"Dispatched $item")
        return Some(item._1)
      }
    }
    None
  }

  def flush(): Unit = {
    this.shelf = new ListBuffer[(ReorderBufferEntry , Boolean)]
  }

  override def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some(xs) => xs.foreach(x => this.shelf.addOne((x, false)))
      case None => ()
    }
    input = None
  }
}
