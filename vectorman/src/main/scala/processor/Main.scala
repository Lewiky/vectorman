package processor

import debugger.Debugger

object Main {
  def main(args: Array[String]): Unit = {
    val instructionMemory = new InstructionMemory(args(0))
    val debugger = new Debugger(instructionMemory)
    debugger.debug()
  }
}
