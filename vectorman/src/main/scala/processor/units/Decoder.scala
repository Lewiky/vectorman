package processor.units

import processor.exceptions.InstructionParseException
import processor.{Instruction, ProgramCounter, logger}
import processor.units.{InstructionParser => Parser}

class Decoder extends EUnit[(String, ProgramCounter), (Instruction, ProgramCounter)] {

  var input: Option[(String, ProgramCounter)] = None
  var output: Option[(Instruction, ProgramCounter)] = None

  private def decodeNext(line: String): Instruction = {
    Parser.parseAll(Parser.instruction, line) match {
      case Parser.Success(matched: Instruction, _) =>
        logger.debug(s"Decoded $matched")
        matched
      case Parser.NoSuccess(msg, _) => throw InstructionParseException(msg)
    }
  }

  def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some((inst, pc)) =>
        output = Some((this.decodeNext(inst), pc))
        input = None
      case None => ()
    }
  }
}