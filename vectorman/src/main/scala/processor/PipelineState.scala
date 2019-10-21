package processor


class PipelineState {
  private var programCounter: ProgramCounter = 0
  private var registerFile: Map[Int, Int] = (for (_ <- 0 to 15) yield (0, 0)).toMap
  private var memoryFile: Map[Int, Int] = Map()

  def increment(amount: Int = 1): Unit = {
    this.programCounter += amount
  }

  def getPc: ProgramCounter = this.programCounter

  def setPc(value: Int): Unit = {
    this.programCounter = value
  }

  def printRegisters(): Unit = println("[ " + (for ((k, v) <- registerFile) yield s"$k: $v, ").mkString("") + "]")

  def getReg(id: Int): Int = this.registerFile(id)

  def setReg(id: Int, value: Int): Unit = {
    this.registerFile += (id -> value)
  }

  def getMem(address: Int): Int = {
    if (!this.memoryFile.contains(address)) return 0
    this.memoryFile(address)
  }

  def setMem(address: Int, value: Int): Unit = {
    this.memoryFile += (address -> value)
  }

}
