package processor

class PipelineState {
  private var programCounter: ProgramCounter = 0

  def increment(amount: Int = 1): Unit = {
    this.programCounter += amount
  }

  def getPc: ProgramCounter = this.programCounter

}
