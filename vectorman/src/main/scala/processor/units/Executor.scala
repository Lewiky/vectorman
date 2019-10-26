package processor.units

import processor._
import processor.ExecutionResult

class Executor(state: PipelineState) {

  private def g(i: Int): Int = state.getReg(i)

  def execute(instruction: Instruction): Option[ExecutionResult] = {
    instruction match {
      case Add(params) => Some(new ExecutionResult(params(0), g(params(1)) + g(params(2))))
      case Sub(params) => Some(new ExecutionResult(params(0), g(params(1)) - g(params(2))))
      case Mul(params) => Some(new ExecutionResult(params(0), g(params(1)) * g(params(2))))
      case Div(params) => Some(new ExecutionResult(params(0), g(params(1)) / g(params(2))))
      case Lod(params) => Some(new ExecutionResult(params(0), state.getMem(g(params(1)) + g(params(2)))))
      case Str(params) =>
        state.setMem(g(params(1)) + g(params(2)), g(params(0)))
        None
      case Bra(params) => Some(new ExecutionResult(PC, g(params(0))))
      case Jmp(params) => Some(new ExecutionResult(PC, g(params(0)) + state.getPc))
      case Ble(params) => if (g(params(1)) <= g(params(2))) {
        Some(new ExecutionResult(PC, g(params(0)) + state.getPc))
      } else None
      case Cmp(params) =>
        var result = 0
        if (g(params(1)) < g(params(2))) result = -1
        if (g(params(1)) > g(params(2))) result = 1
        Some(new ExecutionResult(params(0), result))
      case And(params) => Some(new ExecutionResult(params(0), g(params(1)) & g(params(2))))
      case Not(params) => Some(new ExecutionResult(params(0), ~g(params(1))))
      case Rsh(params) => Some(new ExecutionResult(params(0), g(params(1)) >> g(params(2))))
      case Beq(params) => if (g(params(1)) == g(params(2))) {
        Some(new ExecutionResult(PC, g(params(0)) + state.getPc))
      } else None
      case Cpy(params) => Some(new ExecutionResult(params(0), g(params(1))))
      case Loi(params, immediate) => Some(new ExecutionResult(params(0), immediate))
    }
  }
}
