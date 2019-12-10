package processor

import javax.print.attribute.standard.Destination
import processor.units.circularBuffer.ReorderBufferEntry


class PipelineState {
  private var programCounter: ProgramCounter = 0
  private var registerFile: Map[Int, Int] = (for (_ <- 0 to 15) yield (0, 0)).toMap
  private var memoryFile: Map[Int, Int] = Map()
  private var scoreboard: Map[Int, Option[ReorderBufferEntry]] = Map()
  private var timer: Int = 0
  private var instructions: Int = 0

  def increment(amount: Int = 1): Unit = {
    this.programCounter += amount
  }

  def getPc: ProgramCounter = this.programCounter

  def setPc(value: Int): Unit = {
    this.programCounter = value
  }

  //Yield a string of r{key}: {value} for each tuple in registerFile, concat all the strings and put between []
  def printRegisters(): Unit = {
    println("[ " +
      (for (k <- registerFile.keys.toList.sorted) yield s" ${registerFile(k)} ").mkString("") +
      s"| ${this.getPc}" +
      "]")
    println("[ " +
      (for (k <- registerFile.keys.toList.sorted) yield s" $k ").mkString("") +
      s"| PC" +
      "]")

    //println(s"Scoreboard: ${this.scoreboard}")
  }

  def printScoreboard(): Unit = {
    println("-- Scoreboard --")

    for (item <- this.scoreboard) {
      println(item)
    }
  }

  def reserveScoreboard(entry: ReorderBufferEntry): Unit = {
    if (this.scoreboard.contains(entry.getInstruction.getDestination) && this.scoreboard(entry.getInstruction.getDestination).isDefined) {
      return
    }
    this.scoreboard += (entry.getInstruction.getDestination -> Some(entry))
    logger.debug(s"Scoreboard Reserved: $entry")
  }

  def freeScoreboard(result: ExecutionResult): Unit = {
    var target = result.getTarget
    if(result.getIsMemory) target = 1000
    logger.debug(s"Scoreboard freed: $result($target)")
    this.scoreboard += (target -> None)
  }

  def scoreboardReserved(entry: ReorderBufferEntry): Boolean = {
    entry.getInstruction.getParams foreach {
      param =>
        if (this.scoreboard.contains(param)) {
          this.scoreboard(param) match {
            case Some(value) => if(value.uid < entry.uid) return true
            case None => ()
          }
        }
    }
   this.scoreboardTargetReserved(entry)
  }

  def scoreboardTargetReserved(entry: ReorderBufferEntry): Boolean = {
    if (this.scoreboard.contains(entry.getInstruction.getDestination)) {
      this.scoreboard(entry.getInstruction.getDestination) match {
        case Some(sb_entry) => return sb_entry != entry
        case None => ()
      }
    }
    false
  }

  def flushScoreboard(): Unit = {
    this.scoreboard = Map()
  }

  def printMemory(): Unit = {
    println(s"Memory: ${this.memoryFile}")
  }

  def getReg(id: Int): Int = this.registerFile(id)

  def setReg(id: Int, value: Int): Unit = {
    this.registerFile += (id -> value)
  }

  def getMem(address: Int): Int = {
    val new_address = address + 1000
    if (!this.memoryFile.contains(new_address)) return 0
    logger.debug(s"Returning $new_address: ${this.memoryFile(new_address)}")
    this.memoryFile(new_address)
  }

  def setMem(address: Int, value: Int): Unit = {
    this.memoryFile += (address -> value)
    logger.debug(s"Set memory $address: $value")
  }

  def getTime: Int = this.timer

  def tickTime(amount: Int): Unit = {
    this.timer += amount
    logger.debug("Tick")
  }

  def instructionFinished(): Unit = this.instructions += 1

  def getInstructionsCompleted: Int = this.instructions

}
