package processor.units

import processor._
import processor.units.circularBuffer.ReorderBufferEntry

import scala.collection.mutable.ListBuffer

class ReservationStation(executors: List[Executor], state: PipelineState) extends EUnit[List[ReorderBufferEntry], List[ReorderBufferEntry]] {
  override var input: Option[List[ReorderBufferEntry]] = None
  override var output: Option[List[ReorderBufferEntry]] = None
  private var shelf: ListBuffer[(ReorderBufferEntry, Boolean)] = new ListBuffer[(ReorderBufferEntry, Boolean)]
  private var buff: ListBuffer[ReorderBufferEntry] = new ListBuffer[ReorderBufferEntry]

  private def isNotDependent(reorderBufferEntry: ReorderBufferEntry): Boolean = {
    if(state.scoreboardReserved(reorderBufferEntry)) return false
    //check no other executing instruction writes to the same place (WAW Hazard)
    executors foreach {
      executor =>
        executor.getExecuting match {
          case Some(entry) =>
            if (entry.getInstruction.getDestination == reorderBufferEntry.getInstruction.getDestination) return false
            if (reorderBufferEntry.getInstruction.params.contains(entry.getInstruction.getDestination)) return false
          case None => ()
        }
    }
    // Check that your operands are the most up to date version (RAW Hazard)
    reorderBufferEntry.getInstruction.getParams foreach {
      param =>
        //Check instructions ahead of you in the queue too
        shelf.take(shelf.indexOf((reorderBufferEntry, false))) foreach {
          case (entry, _) => if (entry.getInstruction.getDestination == param) {
            return false
          }
        }
        buff foreach {
          entry =>
            if (entry.getInstruction.getDestination == param) {
              return false
            }
        }
    }
    true
  }

  private def resolveDependencies(): Unit = {
    shelf = shelf map {
      case (entry: ReorderBufferEntry, _: Boolean) =>
        (entry, this.isNotDependent(entry))
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
    buff = new ListBuffer[ReorderBufferEntry]
    for (_ <- 0 until n) {
      this.getNextReadyInstruction match {
        case Some(x) => buff += x
        case None => ()
      }
    }
    buff.toList
  }

  def getNextReadyInstruction: Option[ReorderBufferEntry] = {
    this.resolveDependencies()
    for (item <- this.shelf) {
      if (item._2) {
        this.shelf -= item
        logger.debug(s"Dispatched $item")
        return Some(item._1)
      }
    }
    None
  }

  def flush(): Unit = {
    this.shelf = new ListBuffer[(ReorderBufferEntry, Boolean)]
  }

  override def tick(): Unit = {
    this.shelf foreach(
      x => {
        if(!state.scoreboardTargetReserved(x._1)) state.reserveScoreboard(x._1)
      }
      )
    if (output.isDefined) return
    input match {
      case Some(xs) => xs.foreach(x => this.shelf.addOne((x, false)))
      case None => ()
    }
    input = None
  }

  def print(): Unit = {
    println("Reservation Station:")
    shelf foreach {
      case(item, state) => println(s"$item  | $state")
    }
  }

}
