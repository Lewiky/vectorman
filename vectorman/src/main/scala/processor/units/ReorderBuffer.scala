package processor.units

import processor._
import processor.units.circularBuffer.{CircularBuffer, ReorderBufferEntry}


class ReorderBuffer {

  private var buffer: CircularBuffer[ReorderBufferEntry] = new CircularBuffer[ReorderBufferEntry](100)

  def getNextResult: Option[ExecutionResult] = {
    if (buffer.peek().isFinished) {
      this.buffer.read() match {
        case Some(entry) =>
          logger.debug(s"Released from buffer: ${entry.getInstruction}")
          return entry.getResult
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
