package processor.units

import processor._

class WriteBack(state: PipelineState, pipeline: Pipeline, reorderBuffer: ReorderBuffer) extends EUnit[List[ExecutionResult], Nothing] {

  var input: Option[List[ExecutionResult]] = None
  var output: Option[Nothing] = _

  private def writeResult(result: ExecutionResult): Unit = {
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
    if(this.reorderBuffer.nonEmpty){
      this.reorderBuffer.getNextResult match {
        case Some(result) => this.writeResult(result)
        case None => ()
      }
    }
  }
}
