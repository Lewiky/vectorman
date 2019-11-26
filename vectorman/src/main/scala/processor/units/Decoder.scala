package processor.units

import processor.exceptions.InstructionParseException
import processor.units.circularBuffer.ReorderBufferEntry
import processor.{Instruction, ProgramCounter, logger}
import processor.units.{InstructionParser => Parser}

class Decoder(executors: List[Executor], reorderBuffer: ReorderBuffer) extends EUnit[List[(String, ProgramCounter)], List[ReorderBufferEntry]] {

  var input: Option[List[(String, ProgramCounter)]] = None
  var output: Option[List[ReorderBufferEntry]] = None
  val reservationStation: ReservationStation = new ReservationStation(executors)

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

  private def buildReservationStationInput(lines: List[(String, ProgramCounter)]): List[ReorderBufferEntry] = {
    decodeMany(lines).map(x => reorderBuffer.addItem(x._1, x._2))
  }

  def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some(xs) =>
        reservationStation.input = Some(this.buildReservationStationInput(xs))
        reservationStation.tick()
        input = None
      case None => ()
    }
    val instructions = reservationStation.getNReadyInstructions(executors.count(_.isReady))
    if(instructions.nonEmpty){
      output = Some(instructions)
    } else {
      output = None
    }
  }

}