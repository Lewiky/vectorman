package assembler

import scala.util.parsing.combinator.RegexParsers

class InstructionParser extends RegexParsers {
  def number: Parser[Int] = """[0-9]{1,2}""".r ^^ {
    _.toInt
  }

  def reg: Parser[Register] = "r" ~> number ^^ {
    identity
  }

  def add: Parser[Add] = "ADD" ~ reg ~ reg ~ reg ^^ {
    case "ADD" ~ a ~ b ~ c => Add(List(a, b, c))
  }

  def sub: Parser[Sub] = "SUB" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Sub(List(a, b, c))
  }

  def mul: Parser[Mul] = "MUL" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Mul(List(a, b, c))
  }

  def div: Parser[Div] = "DIV" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Div(List(a, b, c))
  }

  def instruction: Parser[Instruction] = add | sub | mul | div

  def program: Parser[List[Instruction]] = instruction.*
}
