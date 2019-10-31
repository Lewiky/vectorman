package processor.units

import processor._

class Fetcher(state: PipelineState, instructionMemory: InstructionMemory) extends EUnit[Nothing, (String, ProgramCounter)] {

  var input: Option[Nothing] = _
  var output: Option[(String, ProgramCounter)] = None

  private def fetchNext(): (String, ProgramCounter) = {
    val pc = state.getPc
    val inst = instructionMemory.memory(pc)
    logger.debug(s"Fetched: $inst")
    state.increment()
    (inst, pc)
  }

  def tick(): Unit = {
    output match {
      case Some(_) => ()
      case None => output = if(0 <= state.getPc && state.getPc < instructionMemory.memory.length){
        Some(this.fetchNext())
      } else {
        None
      }
    }
  }
}
