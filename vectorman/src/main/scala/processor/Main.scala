package processor

import debugger.Debugger
/*
TODO: Write more ASM programs
TODO: Superscalar
  SUBTODO: Multiple Execute Units
  SUBTODO: Dependency Resolution Algorithm
TODO: Branch Prediction
  SUBTODO: Static Branch
  SUBTODO: Dynamic Branch
  SUBTODO:
*/
object Main {
  def main(args: Array[String]): Unit = {
    val instructionMemory = new InstructionMemory(args(0))
    val debugger = new Debugger(instructionMemory)
    debugger.debug()
  }
}
