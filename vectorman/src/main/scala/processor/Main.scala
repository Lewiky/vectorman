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
    var userMemory: Option[InstructionMemory] = None
    if(args.length == 2){
      userMemory = Some(new InstructionMemory(args(1)))
    }
    val debugger = new Debugger(instructionMemory, userMemory = userMemory)
    debugger.debug()
  }
}
