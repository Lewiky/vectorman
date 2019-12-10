package processor.units.circularBuffer

import processor.{ExecutionResult, Instruction, ProgramCounter}


class ReorderBufferEntry(instruction: Instruction, programCounter: ProgramCounter,val uid: Int) {

  object State extends Enumeration {
    val Finished, Executing, Issued = Value
  }

  private var state = State.Issued
  private var result: Option[ExecutionResult] = None

  def finish(executionResult: ExecutionResult): Unit = {
    state = State.Finished
    result = Some(executionResult)
  }

  def isFinished: Boolean = state == State.Finished

  def execute(): Unit = {
    state = State.Executing
  }

  def getInstruction: Instruction = instruction

  def getPC: ProgramCounter = programCounter

  def getResult: Option[ExecutionResult] = this.result


  override def toString: String = s"UID: $uid, PC: $programCounter, Instruction: $instruction, status: $state"
}