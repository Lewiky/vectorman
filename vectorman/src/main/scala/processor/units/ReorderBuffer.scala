package processor.units

import processor._
import processor.units.circularBuffer.{CircularBuffer, ReorderBufferEntry}


class ReorderBuffer {

  private var buffer: CircularBuffer[ReorderBufferEntry] = new CircularBuffer[ReorderBufferEntry](100)
  private var counter = 0

  def getNextResult: Option[ExecutionResult] = {
    if (buffer.peek().isFinished) {
      this.buffer.read() match {
        case Some(entry) =>
          logger.debug(s"Released from buffer: ${entry.uid}: ${entry.getInstruction}")
          return entry.getResult
        case None => ()
      }
    }
    None
  }

  def addItem(instruction: Instruction, programCounter: ProgramCounter): ReorderBufferEntry = {
    val entry = new ReorderBufferEntry(instruction, programCounter, counter)
    buffer.write(entry)
    counter += 1
    entry
  }

  def isEmpty: Boolean = this.buffer.empty

  def nonEmpty: Boolean = !this.buffer.empty

  def flush(): Unit = {
    this.buffer = new CircularBuffer[ReorderBufferEntry](100)
  }

  def show(): Unit = {
    println("-- ReorderBuffer --")
    this.buffer.show()
  }
}
