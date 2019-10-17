package processor

import scala.collection.mutable.ListBuffer
import scala.io.Source

class InstructionMemory(filename: String){
  private val mem_buffer: ListBuffer[String] = new ListBuffer[String]()
  private val source = Source.fromFile(filename)
  source.getLines().foreach {
    line => mem_buffer += line
  }
  source.close()
  val memory: List[String] = this.mem_buffer.toList
}
