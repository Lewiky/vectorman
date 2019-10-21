package processor.units

import processor._

class Executor(state: PipelineState) {

  private def g(i: Int): Int = state.getReg(i)

  def execute(instruction: Instruction): Unit = {
    instruction match {
      case Add(params) => state.setReg(params(0), g(params(1)) + g(params(2)))
      case Sub(params) => state.setReg(params(0), g(params(1)) - g(params(2)))
      case Mul(params) => state.setReg(params(0), g(params(1)) * g(params(2)))
      case Div(params) => state.setReg(params(0), g(params(1)) / g(params(2)))
      case Lod(params) => state.setReg(params(0), state.getMem(g(params(1)) + g(params(2))))
      case Str(params) => state.setMem(g(params(1)) + g(params(2)), g(params(0)))
      case Bra(params) => state.setPc(g(params(0)))
      case Jmp(params) => state.setPc(g(params(0)) + state.getPc)
      case Ble(params) => if (g(params(1)) <= g(params(2))) {
        state.setPc(g(params(0)) + state.getPc)
      }
      case Cmp(params) =>
        var result = 0
        if (g(params(1)) < g(params(2))) result = -1
        if (g(params(1)) > g(params(2))) result = 1
        state.setReg(params(0), result)
      case And(params) => state.setReg(params(0), g(params(1)) & g(params(2)))
      case Not(params) => state.setReg(params(0), ~g(params(1)))
      case Rsh(params) => state.setReg(params(0), g(params(1)) >> g(params(2)))
      case Beq(params) => if (g(params(1)) == g(params(2))) {
        state.setPc(g(params(0)) + state.getPc)
      }
      case Cpy(params) => state.setReg(params(0), g(params(1)))
      case Loi(params, immediate) => state.setReg(params(0), immediate)
    }
  }
}
