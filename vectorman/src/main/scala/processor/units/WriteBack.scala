package processor.units

import processor._
import processor.units.branchPredictor.BranchPredictor

class WriteBack(state: PipelineState, pipeline: Pipeline, reorderBuffer: ReorderBuffer, branchPredictor: BranchPredictor) extends EUnit[List[ExecutionResult], Nothing] {

  var input: Option[List[ExecutionResult]] = None
  var output: Option[Nothing] = _

  private def writeResult(result: ExecutionResult): Unit = {
    if (result.getTarget == PC) {
      if (result.hasResult) {
        state.setPc(result.getResult)
      }
    }
    else {
      if (result.hasResult) {
        if (result.getIsMemory) {
          state.setMem(result.getTarget, result.getResult)
        } else {
          state.setReg(result.getTarget, result.getResult)
        }
      }
    }
    state.freeScoreboard(result)
    if (result.getTarget == PC && !branchPredictor.wasCorrect(result)){
      branchPredictor.ingest("", state.getPc - 1)
      this.pipeline.flush()
    }
    state.printRegisters()
    state.printMemory()
    state.instructionFinished()
  }

  def tick(): Unit = {
    if (this.reorderBuffer.nonEmpty) {
      this.reorderBuffer.getNextResult match {
        case Some(result) => this.writeResult(result)
        case None => ()
      }
    }
  }
}
