package processor.units

import processor._

class WriteBack(state: PipelineState) {

  def writeResults(result: Option[ExecutionResult]):Unit = {
    result match {
      case Some(result: ExecutionResult) =>
        if(result.getRegister == PC) state.setPc(result.getResult)
        else state.setReg(result.getRegister, result.getResult)
      case None => ()
    }
  }
}
