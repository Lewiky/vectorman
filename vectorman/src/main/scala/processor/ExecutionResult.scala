package processor

class ExecutionResult(target: Option[Int], result: Option[Int], programCounter: ProgramCounter, isMemory: Boolean = false) {
  def getResult: Int = this.result.someOr(-1)
  def hasResult: Boolean = this.result.isDefined
  def getTarget: ProgramCounter = this.target.someOr(-2)
  def getIsMemory: Boolean = this.isMemory
  def getPC: ProgramCounter = this.programCounter
}
