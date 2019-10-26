package processor

class ExecutionResult(register: Register, result: Int) {
  def getResult: Int = this.result
  def getRegister: Register = this.register
}
