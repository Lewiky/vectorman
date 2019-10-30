package processor.units

import processor.exceptions.InstructionParseException
import processor.Instruction
import processor.logger

class Decoder extends EUnit[String, Instruction] {
  private val parser: InstructionParser = new InstructionParser()

  var input: Option[String] = None
  var output: Option[Instruction] = None

  private def decodeNext(line: String): Instruction = {
    this.parser.parse(this.parser.instruction, line) match {
      case this.parser.Success(matched: Instruction, _) =>
        logger.debug(s"Decoded $matched")
        matched
      case this.parser.Failure(msg, _) => throw InstructionParseException(msg)
      case this.parser.Error(msg, _) => throw InstructionParseException(msg)
    }
  }

  def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some(inst) =>
        output = Some(this.decodeNext(inst))
        input = None
      case None => ()
    }
  }
}