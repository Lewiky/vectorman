package processor.units

import processor._

import scala.util.parsing.combinator.RegexParsers

object InstructionParser extends RegexParsers {

  def number: Parser[Int] =
    """[-]?[0-9]{1,10}""".r ^^ {
      _.toInt
    }

  def reg: Parser[Register] = "r" ~> number ^^ {
    identity
  }

  def add: Parser[Add] = "ADD" ~ reg ~ reg ~ reg ^^ {
    case "ADD" ~ a ~ b ~ c => Add(List(a, b, c), "ADD")
  }

  def sub: Parser[Sub] = "SUB" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Sub(List(a, b, c), "SUB")
  }

  def mul: Parser[Mul] = "MUL" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Mul(List(a, b, c), "MUL")
  }

  def div: Parser[Div] = "DIV" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Div(List(a, b, c), "DIV")
  }

  def lod: Parser[Lod] = "LOD" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Lod(List(a, b, c), "LOD")
  }

  def str: Parser[Str] = "STR" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Str(List(a, b, c), "STR")
  }

  def bra: Parser[Bra] = "BRA" ~> reg ^^ (a => Bra(List(a), "BRA"))

  def jmp: Parser[Jmp] = "JMP" ~> reg ^^ (a => Jmp(List(a), "JMP"))

  def ble: Parser[Ble] = "BLE" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Ble(List(a, b, c), "BLE")
  }

  def cmp: Parser[Cmp] = "CMP" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Cmp(List(a, b, c), "CMP")
  }

  def and: Parser[And] = "AND" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => And(List(a, b, c), "AND")
  }

  def not: Parser[Not] = "NOT" ~> reg ~ reg ^^ {
    case a ~ b => Not(List(a, b), "NOT")
  }

  def rsh: Parser[Rsh] = "RSH" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Rsh(List(a, b, c), "RSH")
  }

  def beq: Parser[Beq] = "BEQ" ~> reg ~ reg ~ reg ^^ {
    case a ~ b ~ c => Beq(List(a, b, c), "BEQ")
  }

  def cpy: Parser[Cpy] = "CPY" ~> reg ~ reg ^^ {
    case a ~ b => Cpy(List(a, b), "CPY")
  }

  def loi: Parser[Loi] = "LOI" ~> reg ~ number ^^ {
    case a ~ im => Loi(List(a),"LOI", im)
  }

  def end: Parser[End] = "END" ^^ { _ => End(List(), "END") }

  def instruction: Parser[Instruction] = add | sub | mul | div |
    lod | str | bra | jmp |
    ble | cmp | and | not |
    rsh | beq | cpy | loi |
    end

  def program: Parser[List[Option[Instruction]]] = instruction.?.*
}
