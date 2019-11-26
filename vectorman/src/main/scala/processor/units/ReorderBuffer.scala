package processor.units

import processor.{ExecutionResult, Instruction, ProgramCounter}
import processor.units.circularBuffer.{CircularBuffer, ReorderBufferEntry}


class ReorderBuffer {

  private var buffer: CircularBuffer[ReorderBufferEntry] = new CircularBuffer[ReorderBufferEntry](100)

  def getNextResult: Option[ExecutionResult] = {
    if(buffer.peek().isFinished){
      this.buffer.read() match {
        case Some(entry) => return entry.getResult
        case None => ()
      }
    }
    None
  }

  def addItem(instruction: Instruction, programCounter: ProgramCounter): ReorderBufferEntry = {
    val entry = new ReorderBufferEntry(instruction, programCounter)
    buffer.write(entry)
    entry
  }

  def isEmpty: Boolean = this.buffer.empty

  def nonEmpty: Boolean = !this.buffer.empty

  def flush(): Unit = {
    this.buffer = new CircularBuffer[ReorderBufferEntry](100)
  }
}
