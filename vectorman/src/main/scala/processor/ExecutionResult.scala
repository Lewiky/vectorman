package processor

class ExecutionResult(target: Option[Int], result: Option[Int], programCounter: ProgramCounter, text: String, uid: Int, isMemory: Boolean = false) {
  def getResult: Int = this.result.someOr(this.programCounter + 1)

  def hasResult: Boolean = this.result.isDefined

  def getTarget: Register = {
    if (this.isMemory) {
      return this.target.get + 1000
    }
    this.target.someOr(-2)
  }

  def getIsMemory: Boolean = this.isMemory

  def getPC: ProgramCounter = this.programCounter

  def getText: String = this.text

  override def toString: String = s"($uid) Result: Set $target to $result"
}
