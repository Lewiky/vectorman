package processor


class PipelineState {
  private var programCounter: ProgramCounter = 0
  private var registerFile: Map[Int, Int] = (for (_ <- 0 to 15) yield (0, 0)).toMap
  private var memoryFile: Map[Int, Int] = Map()
  private var timer: Int = 0

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
  }

  def getReg(id: Int): Int = this.registerFile(id)

  def setReg(id: Int, value: Int): Unit = {
    this.registerFile += (id -> value)
  }

  def getMem(address: Int): Int = {
    if (!this.memoryFile.contains(address)) return 0
    logger.debug(s"Returning $address: ${this.memoryFile(address)}")
    this.memoryFile(address)
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
}
