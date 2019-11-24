package processor.units

import processor.exceptions.InstructionParseException
import processor.{Instruction, ProgramCounter, logger}
import processor.units.{InstructionParser => Parser}

class Decoder extends EUnit[List[(String, ProgramCounter)], List[(Instruction, ProgramCounter)]] {

  var input: Option[List[(String, ProgramCounter)]] = None
  var output: Option[List[(Instruction, ProgramCounter)]] = None

  private def decodeNext(line: String): Instruction = {
    Parser.parseAll(Parser.instruction, line) match {
      case Parser.Success(matched: Instruction, _) =>
        logger.debug(s"Decoded $matched")
        matched
      case Parser.NoSuccess(msg, _) => throw InstructionParseException(msg)
    }
  }

  private def decodeMany(lines: List[(String, ProgramCounter)]): List[(Instruction, ProgramCounter)] = {
    lines.map(x => (this.decodeNext(x._1), x._2))
  }

  def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some(xs) =>
        output = Some(this.decodeMany(xs))
        input = None
      case None => ()
    }
  }
}