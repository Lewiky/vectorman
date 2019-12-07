package processor

sealed trait Instruction {
  val params: List[Register]
  def
  getDestination: Register = params(0)
  def getParams: List[Register] = params.slice(1, params.length)
}

case class Add(params: List[Register]) extends Instruction

case class Sub(params: List[Register]) extends Instruction

case class Mul(params: List[Register]) extends Instruction

case class Div(params: List[Register]) extends Instruction

case class Lod(params: List[Register]) extends Instruction

case class Str(params: List[Register]) extends Instruction {
  override def getDestination: Register = params(1) + params(2) + 1000 //Big old offset for memory addresses
  override def getParams: List[Register] = params
}

case class Bra(params: List[Register]) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = List(params(0))
}

case class Jmp(params: List[Register]) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = List(params(0))
}

case class Ble(params: List[Register]) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = params
}

case class Cmp(params: List[Register]) extends Instruction

case class And(params: List[Register]) extends Instruction

case class Not(params: List[Register]) extends Instruction

case class Rsh(params: List[Register]) extends Instruction

case class Beq(params: List[Register]) extends Instruction {
  override def getDestination: Register = PC
  override def getParams: List[Register] = List(params(0))
}

case class Cpy(params: List[Register]) extends Instruction

case class Loi(params: List[Register], immediate: Int) extends Instruction

case class End(params: List[Register]) extends Instruction {
  override def getDestination: Register = PC
}
