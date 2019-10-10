package assembler
import scala.util.parsing.combinator.RegexParsers

class InstructionParser extends RegexParsers{
  def number: Parser[Int] = """[0-9]{1,2}""".r ^^ {_.toInt}
  def reg: Parser[Register] = "r" ~> number ^^ {_.toByte}
  def add: Parser[Add] = "ADD" ~  reg ~ reg ~ reg ^^ {
    case "ADD" ~ a ~ b ~ c => Add(a, b, c)
  }
  def sub: Parser[Sub] = "SUB" ~  reg ~ reg ~ reg ^^ {
    case "SUB" ~ a ~ b ~ c => Sub(a, b, c)
  }
  def mul: Parser[Mul] = "MUL" ~  reg ~ reg ~ reg ^^ {
    case "MUL" ~ a ~ b ~ c => Mul(a, b, c)
  }
  def div: Parser[Div] = "DIV" ~  reg ~ reg ~ reg ^^ {
    case "DIV" ~ a ~ b ~ c => Div(a, b, c)
  }
  def instruction: Parser[Instruction] = add | sub | mul | div
  def program: Parser[List[Instruction]] = instruction.*
}
