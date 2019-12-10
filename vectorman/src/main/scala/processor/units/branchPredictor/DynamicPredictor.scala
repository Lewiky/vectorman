package processor.units.branchPredictor
import processor.{ExecutionResult, ProgramCounter}

class DynamicPredictor extends BranchPredictor {

  //Finite State Machine
  object FSMState extends Enumeration {
    val STRONG_TAKEN, WEAK_TAKEN, WEAK_NOT, STRONG_NOT = Value
  }

  import FSMState._

  private var states: Map[ProgramCounter, Value] = Map()

  private def transition(state: Value, taken: Boolean): Value = {
    state match {
      case STRONG_TAKEN => if(taken) STRONG_TAKEN else WEAK_TAKEN
      case WEAK_TAKEN   => if(taken) STRONG_TAKEN else WEAK_NOT
      case WEAK_NOT     => if(taken) WEAK_TAKEN   else STRONG_NOT
      case STRONG_NOT   => if(taken) WEAK_NOT     else STRONG_NOT
    }
  }

  override def wasCorrect(executionResult: ExecutionResult): Boolean = {
    val result = super.wasCorrect(executionResult)
    if(!this.states.contains(executionResult.getPC)){
      this.states += (executionResult.getPC -> STRONG_TAKEN)
      return result
    }
    this.states += (executionResult.getPC -> transition(this.states(executionResult.getPC), executionResult.hasResult))
    result
  }

  override protected def predictTaken(string: String): Boolean = {
    val unconditionalBranches = List("BRA", "JMP", "END")
    unconditionalBranches.foreach(branch => if (string.contains(branch)) return true)
    if(!this.states.contains(lastSeen._2)){
      this.states += (lastSeen._2 -> STRONG_TAKEN)
    }
    this.states(lastSeen._2) match {
      case STRONG_TAKEN | WEAK_TAKEN => true
      case STRONG_NOT   | WEAK_NOT   => false
    }
  }

}
