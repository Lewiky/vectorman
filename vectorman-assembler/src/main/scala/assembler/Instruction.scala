package assembler

sealed trait Instruction {
  val bin: String
  val params: List[Register]
}

case class Add(params: List[Register], bin: String = "0000") extends Instruction

case class Sub(params: List[Register], bin: String = "0001") extends Instruction

case class Mul(params: List[Register], bin: String = "0010") extends Instruction

case class Div(params: List[Register], bin: String = "0011") extends Instruction

case class Lod(params: List[Register], bin: String = "0100") extends Instruction

case class Str(params: List[Register], bin: String = "0101") extends Instruction

case class Bra(params: List[Register], bin: String = "0110") extends Instruction

case class Jmp(params: List[Register], bin: String = "0111") extends Instruction

case class Ble(params: List[Register], bin: String = "1000") extends Instruction

case class Cmp(params: List[Register], bin: String = "1001") extends Instruction

case class And(params: List[Register], bin: String = "1010") extends Instruction

case class Not(params: List[Register], bin: String = "1011") extends Instruction

case class Rsh(params: List[Register], bin: String = "1011") extends Instruction

case class Beq(params: List[Register], bin: String = "1100") extends Instruction

case class Cpy(params: List[Register], bin: String = "1110") extends Instruction


