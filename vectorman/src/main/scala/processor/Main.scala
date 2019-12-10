package processor

import debugger.Debugger
/*
TODO: Write more ASM programs
TODO: Branch Prediction
  SUBTODO: Dynamic Branch
TODO: Register Renaming/Tomosulo Algorithm
*/
object Main {
  def main(args: Array[String]): Unit = {
    val instructionMemory = new InstructionMemory(args(0))
    val debugger = new Debugger(instructionMemory)
    debugger.debug()
  }
}
