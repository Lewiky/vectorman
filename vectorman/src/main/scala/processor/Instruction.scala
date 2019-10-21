package processor

sealed trait Instruction {
  val params: List[Register]
}

case class Add(params: List[Register]) extends Instruction

case class Sub(params: List[Register]) extends Instruction

case class Mul(params: List[Register]) extends Instruction

case class Div(params: List[Register]) extends Instruction

case class Lod(params: List[Register]) extends Instruction

case class Str(params: List[Register]) extends Instruction

case class Bra(params: List[Register]) extends Instruction

case class Jmp(params: List[Register]) extends Instruction

case class Ble(params: List[Register]) extends Instruction

case class Cmp(params: List[Register]) extends Instruction

case class And(params: List[Register]) extends Instruction

case class Not(params: List[Register]) extends Instruction

case class Rsh(params: List[Register]) extends Instruction

case class Beq(params: List[Register]) extends Instruction

case class Cpy(params: List[Register]) extends Instruction

case class Loi(params: List[Register], immediate: Int) extends Instruction


