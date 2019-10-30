package processor.units

import processor._

class Fetcher(state: PipelineState, instructionMemory: InstructionMemory) extends EUnit[Nothing, String] {

  var input: Option[Nothing] = _
  var output: Option[String] = None

  private def fetchNext(): String = {
    val inst = instructionMemory.memory(state.getPc)
    logger.debug(s"Fetched: $inst")
    state.increment()
    inst
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
