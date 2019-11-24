package processor.units

import processor._

import scala.collection.mutable.ListBuffer

class ReservationStation(executors: List[Executor]) extends EUnit[List[(Instruction, ProgramCounter)], List[(Instruction, ProgramCounter)]] {
  override var input: Option[List[(Instruction, ProgramCounter)]] = None
  override var output: Option[List[(Instruction, ProgramCounter)]] = None
  private var shelf: ListBuffer[(Instruction, ProgramCounter, Boolean)] = new ListBuffer[(Instruction, ProgramCounter, Boolean)]

  private def resolveDependencies(): List[(Instruction, ProgramCounter)] = {
    val readyUnits = executors.count(_.isReady)
    if(shelf.nonEmpty){
      shelf(0) = (shelf(0)._1, shelf(0)._2, true) //TODO: Actually
    }
    val results = shelf.filter(_._3 == true).slice(0, readyUnits).map(x => (x._1, x._2)).toList
    shelf = shelf.drop(readyUnits)
    results
  }

  def pipe(): Unit = {
    /*
    Pass all of the ready instructions forward to the ready execute units.
    Any instructions that don't have a ready unit to be dispatched to get put back on the shelf
     */
    var removed = new ListBuffer[(Instruction, ProgramCounter)]
    this.output match {
      case Some(output) => (executors.filter(_.isReady) zip output).foreach {
        case (executor: Executor, out: (Instruction, ProgramCounter)) => if (executor.input.isEmpty) {
          executor.input = Some(out)
          removed += out
        }
      }
        //Find which instructions couldn't be dispatched and reserve them again
        val notPiped: ListBuffer[(Instruction, ProgramCounter)] = (output diff removed).to(ListBuffer)
        this.shelf = this.shelf ++: notPiped.map(x => (x._1, x._2, true))
        this.output = None
      case None => ()
    }
  }

  def flush(): Unit = {
    this.shelf = new ListBuffer[(Instruction, ProgramCounter, Boolean)]
  }

  override def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some(xs) => xs.foreach(x => this.shelf.addOne((x._1, x._2, false)))
      case None => ()
    }
    input = None
    val readyInstructions = this.resolveDependencies()
    output = if (readyInstructions.isEmpty) None else Some(readyInstructions)
  }
}
