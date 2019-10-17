package processor.units

import processor.exceptions.InstructionParseException
import processor.Instruction

class Decoder {
  val parser: InstructionParser = new InstructionParser()

  def decodeNext(line: String): Instruction = {
    this.parser.parse(this.parser.instruction, line) match {
      case this.parser.Success(matched: Instruction, _) => matched
      case this.parser.Failure(msg, _) => throw InstructionParseException(msg)
      case this.parser.Error(msg, _) => throw InstructionParseException(msg)
    }
  }
}