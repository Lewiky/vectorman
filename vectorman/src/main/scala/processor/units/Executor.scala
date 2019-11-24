package processor.units

import processor._
import processor.ExecutionResult

class Executor(state: PipelineState) extends EUnit[(Instruction, ProgramCounter), ExecutionResult] {

  var input: Option[(Instruction, ProgramCounter)] = None
  var output: Option[ExecutionResult] = None

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

  private def execute(instruction: Instruction, programCounter: ProgramCounter): ExecutionResult = {
    logger.debug(s"Executed: $instruction")
    var register: Option[Register] = None
    var result: Option[Int] = None
    instruction match {
      case Add(params) => register = Some(params(0)); result = Some(g(params(1)) + g(params(2)))
      case Sub(params) => register = Some(params(0)); result = Some(g(params(1)) - g(params(2)))
      case Mul(params) => register = Some(params(0)); result = Some(g(params(1)) * g(params(2)))
      case Div(params) => register = Some(params(0)); result =  Some(g(params(1)) / g(params(2)))
      case Lod(params) => register = Some(params(0)); result = Some(state.getMem(g(params(1)) + g(params(2))))
      case Str(params) =>
        state.setMem(g(params(1)) + g(params(2)), g(params(0)))
      case Bra(params) => register = Some(PC); result = Some(g(params(0)))
      case Jmp(params) => register = Some(PC); result =  Some(g(params(0)) + programCounter)
      case Ble(params) => if (g(params(1)) <= g(params(2))) {
        register = Some(PC); result = Some(g(params(0)) + programCounter)
      }
      case Cmp(params) =>
        var comp = 0
        if (g(params(1)) < g(params(2))) comp = -1
        if (g(params(1)) > g(params(2))) comp = 1
        register = Some(params(0)); result = Some(comp)
      case And(params) => register = Some(params(0)); result =  Some(g(params(1)) & g(params(2)))
      case Not(params) => register = Some(params(0)); result = Some(~g(params(1)))
      case Rsh(params) => register = Some(params(0)); result = Some(g(params(1)) >> g(params(2)))
      case Beq(params) => if (g(params(1)) == g(params(2))) {
        register = Some(PC); result = Some(g(params(0)) + programCounter)
      }
      case Cpy(params) => register = Some(params(0)); result = Some(g(params(1)))
      case Loi(params, immediate) => register = Some(params(0)); result = Some(immediate)
      case End(_) => register = Some(PC);result = Some(-2)
    }
    new ExecutionResult(register, result)
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

  def isReady: Boolean = this.executing._1.isEmpty && this.input.isEmpty

  def flush(): Unit = {
    this.executing = (None, 1)
  }
}
