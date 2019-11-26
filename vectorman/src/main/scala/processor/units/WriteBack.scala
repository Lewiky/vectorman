package processor.units

import processor._

class WriteBack(state: PipelineState, pipeline: Pipeline) extends EUnit[ExecutionResult, Nothing] {

  var input: Option[ExecutionResult] = None
  var output: Option[Nothing] = _

  private def writeResults(result: ExecutionResult): Unit = {
    val startPc = state.getPc
    if (result.getTarget == PC) state.setPc(result.getResult)
    else {
      if(result.hasResult){
        if(result.getIsMemory){
          state.setMem(result.getTarget, result.getResult)
        } else {
          state.setReg(result.getTarget, result.getResult)
        }
      }
    }
    if (startPc != state.getPc) this.pipeline.flush()
    state.printRegisters()
    state.printMemory()
    state.instructionFinished()
  }

  def tick(): Unit = {
    if (this.input.isDefined) {
      this.writeResults(this.input.get)
      this.input = None
    }
  }
}
