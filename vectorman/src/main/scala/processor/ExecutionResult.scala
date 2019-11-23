package processor

class ExecutionResult(register: Option[Register], result: Option[Int]) {
  def getResult: Int = this.result.someOr(-1)
  def hasResult: Boolean = this.result.isDefined
  def getRegister: Register = this.register.someOr(-2)
}
