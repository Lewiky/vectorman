package processor.units

import processor._
import processor.units.circularBuffer.{CircularBuffer, ReorderBufferEntry}

import scala.collection.mutable.ListBuffer


class ReorderBuffer {

  private var buffer: CircularBuffer[ReorderBufferEntry] = new CircularBuffer[ReorderBufferEntry](100)
  private var counter = 0

  def getNextResults: List[ExecutionResult] = {
    var buff: ListBuffer[ExecutionResult] = new ListBuffer()
    while (buffer.peek() != null && buffer.peek().isFinished && !buffer.empty) {
      this.buffer.read() match {
        case Some(entry) =>
          logger.debug(s"Released from buffer: ${entry.uid}: ${entry.getInstruction}")
          entry.getResult match {
            case Some(result) => buff += result
            case None => ()
          }
        case None => ()
      }
    }
    buff.toList
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

  def instructionsSeen(): Int = this.counter
}
