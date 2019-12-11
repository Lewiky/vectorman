package processor

sealed trait Instruction {
  val params: List[Register]
  val text: String
  def getDestination: Register = params(0)
  def getParams: List[Register] = params.slice(1, params.length)
}

case class Add(params: List[Register], text: String) extends Instruction

case class Sub(params: List[Register], text: String) extends Instruction

case class Mul(params: List[Register], text: String) extends Instruction

case class Div(params: List[Register], text: String) extends Instruction

case class Lod(params: List[Register], text: String) extends Instruction

case class Str(params: List[Register], text: String) extends Instruction {
  override def getDestination: Register = MEM
  override def getParams: List[Register] = params
}

case class Bra(params: List[Register], text: String )extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = List(params(0))
}

case class Jmp(params: List[Register], text: String) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = List(params(0))
}

case class Ble(params: List[Register], text: String) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = params
}

case class Cmp(params: List[Register], text: String) extends Instruction

case class And(params: List[Register], text: String) extends Instruction

case class Not(params: List[Register], text: String) extends Instruction

case class Rsh(params: List[Register], text: String) extends Instruction

case class Beq(params: List[Register], text: String) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = params
}

case class Cpy(params: List[Register], text: String) extends Instruction

case class Loi(params: List[Register], text: String, immediate: Int) extends Instruction

case class End(params: List[Register], text: String) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = List(PC)
}
