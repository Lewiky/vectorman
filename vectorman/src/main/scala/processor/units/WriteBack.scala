package processor.units

import processor._

class WriteBack(state: PipelineState, pipeline: Pipeline) extends EUnit[Option[ExecutionResult], Nothing] {

  var input: Option[Option[ExecutionResult]] = None
  var output: Option[Nothing] = _

  private def writeResults(result: Option[ExecutionResult]): Unit = {
    val startPc = state.getPc
    result match {
      case Some(result: ExecutionResult) =>
        if (result.getRegister == PC) state.setPc(result.getResult)
        else state.setReg(result.getRegister, result.getResult)
      case None => ()
    }
    if(startPc != state.getPc) this.pipeline.flush()
    state.printRegisters()
  }

  def tick(): Unit = {
    if (this.input.isDefined) {
      this.writeResults(this.input.get)
      this.input = None
    }
  }
}
