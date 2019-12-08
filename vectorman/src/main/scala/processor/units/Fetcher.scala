package processor.units

import processor._
import processor.units.branchPredictor.BranchPredictor

class Fetcher(state: PipelineState, instructionMemory: InstructionMemory, instructionsPerCycle: Int, branchPredictor: BranchPredictor) extends EUnit[Nothing, List[(String, ProgramCounter)]] {

  var input: Option[Nothing] = _
  var output: Option[List[(String, ProgramCounter)]] = None

  private def fetchNext(): (String, ProgramCounter) = {
    val pc = branchPredictor.predict()
    val inst = instructionMemory.memory(pc)
    logger.debug(s"Fetched: $inst")
    state.increment()
    branchPredictor.ingest(inst, pc)
    (inst, pc)
  }

  private def fetchMany(): List[(String, ProgramCounter)] = {
    (for (_ <- 0.until(instructionsPerCycle) if state.getPc < instructionMemory.memory.length) yield fetchNext()).toList
  }

  def tick(): Unit = {
    output match {
      case Some(_) => ()
      case None => output = if (0 <= state.getPc && state.getPc < instructionMemory.memory.length) {
        Some(this.fetchMany())
      } else {
        None
      }
    }
  }
}
