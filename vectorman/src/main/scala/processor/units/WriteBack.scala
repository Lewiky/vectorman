package processor.units

import processor._
import processor.units.branchPredictor.BranchPredictor

class WriteBack(state: PipelineState, pipeline: Pipeline, reorderBuffer: ReorderBuffer, branchPredictor: BranchPredictor) extends EUnit[List[ExecutionResult], Nothing] {

  var input: Option[List[ExecutionResult]] = None
  var output: Option[Nothing] = _
  private var flushed = false

  private def writeResult(result: ExecutionResult): Unit = {
    logger.debug(s"Writing Back: ${result}")
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
    if (result.getTarget == PC && !branchPredictor.wasCorrect(result) && state.getPc > 0) {
      if (!result.hasResult) {
        branchPredictor.ingest(result.getText, result.getPC - 1)
        state.setPc(result.getPC)
      } else {
        branchPredictor.ingest(result.getText, state.getPc - 1)
      }
      this.pipeline.flush()
      flushed = true
    }
    state.instructionFinished()
  }

  def tick(): Unit = {
    if (this.reorderBuffer.nonEmpty) {
      this.reorderBuffer.getNextResults foreach  {
        result => if(!flushed) this.writeResult(result)
      }
      flushed = false
      state.printRegisters()
      state.printMemory()
    }
  }
}
