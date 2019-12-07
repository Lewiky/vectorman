package processor.units

import processor._
import processor.ExecutionResult
import processor.units.circularBuffer.ReorderBufferEntry

class Executor(state: PipelineState) extends EUnit[ReorderBufferEntry, ExecutionResult] {

  var input: Option[ReorderBufferEntry] = None
  var output: Option[ExecutionResult] = None

  private var executing: (Option[ReorderBufferEntry], Int) = Tuple2(None, 0)

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

  private def execute(entry: ReorderBufferEntry): Unit = {
    val instruction: Instruction = entry.getInstruction
    val programCounter: ProgramCounter = entry.getPC
    logger.debug(s"Executed: $instruction")
    var register: Option[Register] = None
    var result: Option[Int] = None
    instruction match {
      case Add(params) => register = Some(params(0)); result = Some(g(params(1)) + g(params(2)))
      case Sub(params) => register = Some(params(0)); result = Some(g(params(1)) - g(params(2)))
      case Mul(params) => register = Some(params(0)); result = Some(g(params(1)) * g(params(2)))
      case Div(params) => register = Some(params(0)); result = Some(g(params(1)) / g(params(2)))
      case Lod(params) => register = Some(params(0)); result = Some(state.getMem(g(params(1)) + g(params(2))))
      case Str(params) =>
        entry.finish(new ExecutionResult(Some(g(params(1)) + g(params(2))), Some(g(params(0))), programCounter, isMemory = true))
        return
      case Bra(params) => register = Some(PC); result = Some(g(params(0)))
      case Jmp(params) => register = Some(PC); result = Some(g(params(0)) + programCounter)
      case Ble(params) =>
        register = Some(PC)
        if (g(params(1)) <= g(params(2))) {
          result = Some(g(params(0)) + programCounter)
        }
      case Cmp(params) =>
        var comp = 0
        if (g(params(1)) < g(params(2))) comp = -1
        if (g(params(1)) > g(params(2))) comp = 1
        register = Some(params(0))
        result = Some(comp)
      case And(params) => register = Some(params(0)); result = Some(g(params(1)) & g(params(2)))
      case Not(params) => register = Some(params(0)); result = Some(~g(params(1)))
      case Rsh(params) => register = Some(params(0)); result = Some(g(params(1)) >> g(params(2)))
      case Beq(params) =>
        register = Some(PC)
        if (g(params(1)) == g(params(2))) {
          result = Some(g(params(0)) + programCounter)
        }
      case Cpy(params) => register = Some(params(0)); result = Some(g(params(1)))
      case Loi(params, immediate) => register = Some(params(0)); result = Some(immediate)
      case End(_) => register = Some(PC); result = Some(-2)
    }
    entry.finish(new ExecutionResult(register, result, programCounter))
  }

  def tick(): Unit = {
    if (input.isEmpty || output.isDefined) return
    this.executing._1 match {
      case Some(entry) =>
        if (this.instructionLength(entry.getInstruction) > this.executing._2) {
          entry.execute()
          this.executing = this.executing.copy(_2 = this.executing._2 + 1)
          logger.debug(s"Executing: ${this.executing}")
          this.output = None
        }
        else {
          this.execute(entry)
          executing = (None, 1)
          input = None
        }
      case None => this.executing = (input, 1)
    }
  }

  def isReady: Boolean = this.executing._1.isEmpty && this.input.isEmpty

  def getExecuting: Option[ReorderBufferEntry] = {
    this.executing._1 match {
      case Some(x) => Some(x)
      case None => this.input match {
        case Some(x) => Some(x)
        case None => None
      }
    }
  }

  def flush(): Unit = {
    this.executing = (None, 1)
  }
}
