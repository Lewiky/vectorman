package processor.units

import processor.{InstructionMemory, PipelineState}

class Fetcher(state: PipelineState, instructionMemory: InstructionMemory) {

  def fetchNext(): String = {
    instructionMemory.memory(state.getPc)
  }
}
