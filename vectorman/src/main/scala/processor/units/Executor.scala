package processor.units

import processor._
import processor.ExecutionResult

class Executor(state: PipelineState) extends EUnit[(Instruction, ProgramCounter), Option[ExecutionResult]] {

  var input: Option[(Instruction, ProgramCounter)] = None
  var output: Option[Option[ExecutionResult]] = None

  private var executing: (Option[(Instruction,ProgramCounter)], Int) = Tuple2(None, 0)

  private def g(i: Int): Int = state.getReg(i)

  private def instructionLength(i: Instruction): Int = {
    i match {
      case Add(_) => 1
      case Sub(_) => 1
      case Div(_) => 24
      case Mul(_) => 3
      case Lod(_) => 4
      case Str(_) => 4
      case Bra(_) => 1
      case Jmp(_) => 1
      case Ble(_) => 2
      case Cmp(_) => 1
      case And(_) => 1
      case Not(_) => 1
      case Rsh(_) => 1
      case Beq(_) => 2
      case Cpy(_) => 2
      case Loi(_, _) => 1
      case End(_) => 1
    }
  }

  private def execute(instruction: Instruction, programCounter: ProgramCounter): Option[ExecutionResult] = {
    logger.debug(s"Executed: $instruction")
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
      case Jmp(params) => Some(new ExecutionResult(PC, g(params(0)) + programCounter))
      case Ble(params) => if (g(params(1)) <= g(params(2))) {
        Some(new ExecutionResult(PC, g(params(0)) + programCounter))
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
        Some(new ExecutionResult(PC, g(params(0)) + programCounter))
      } else None
      case Cpy(params) => Some(new ExecutionResult(params(0), g(params(1))))
      case Loi(params, immediate) => Some(new ExecutionResult(params(0), immediate))
      case End(_) => Some(new ExecutionResult(PC, -2))
    }
  }

  def tick(): Unit = {
    if (input.isEmpty || output.isDefined) return
    this.executing._1 match {
      case Some((instruction, pc)) =>
        if (this.instructionLength(instruction) > this.executing._2) {
          this.executing = this.executing.copy(_2 = this.executing._2 + 1)
          logger.debug(s"Executing: ${this.executing}")
          this.output = None
        }
        else {
          output = Some(this.execute(instruction, pc))
          executing = (None, 1)
          input = None
        }
      case None => this.executing = (input, 1)
    }
  }

  def flush(): Unit = {
    this.executing = (None, 1)
  }
}
