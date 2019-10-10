package assembler
sealed trait Instruction
case class Add(a: Register, b: Register, c: Register) extends Instruction
case class Sub(a: Register, b: Register, c: Register) extends Instruction
case class Mul(a: Register, b: Register, c: Register) extends Instruction
case class Div(a: Register, b: Register, c: Register) extends Instruction
case class Lod(a: Register, b: Register, c: Register) extends Instruction
case class Str(a: Register, b: Register, c: Register) extends Instruction
case class Bra(a: Register)                           extends Instruction
case class Jmp(a: Register)                           extends Instruction
case class Ble(a: Register, b: Register, c: Register) extends Instruction
case class Cmp(a: Register, b: Register, c: Register) extends Instruction
case class And(a: Register, b: Register, c: Register) extends Instruction
case class Not(a: Register, b: Register)              extends Instruction
case class Rsh(a: Register, b: Register, c: Register) extends Instruction
case class Beq(a: Register, b: Register, c: Register) extends Instruction
case class Cpy(a: Register, b: Register)              extends Instruction


