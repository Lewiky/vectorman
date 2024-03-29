package processor.units

import processor.exceptions.InstructionParseException
import processor.units.circularBuffer.ReorderBufferEntry
import processor.{Instruction, PipelineState, ProgramCounter, logger}
import processor.units.{InstructionParser => Parser}

class Decoder(executors: List[Executor], reorderBuffer: ReorderBuffer, state: PipelineState) extends EUnit[List[(String, ProgramCounter)], List[ReorderBufferEntry]] {

  var input: Option[List[(String, ProgramCounter)]] = None
  var output: Option[List[ReorderBufferEntry]] = None
  val reservationStation: ReservationStation = new ReservationStation(executors, state)
  val registerRenameUnit: RegisterRenameUnit = new RegisterRenameUnit
  var renamingEnabled: Boolean = false

  private def decodeNext(line: String): Instruction = {
    Parser.parseAll(Parser.instruction, line) match {
      case Parser.Success(matched: Instruction, _) =>
        logger.debug(s"Decoded $matched")
        matched
      case Parser.NoSuccess(msg, _) => throw InstructionParseException(msg)
    }
  }

  private def decodeMany(lines: List[(String, ProgramCounter)]): List[(Instruction, ProgramCounter)] = {
    lines.map(x => if(!renamingEnabled){
      (this.decodeNext(x._1), x._2)
    } else {
      (registerRenameUnit.rename(this.decodeNext(x._1)), x._2)
    })
  }

  private def buildReservationStationInput(lines: List[(String, ProgramCounter)]): List[ReorderBufferEntry] = {
    val xs = decodeMany(lines).map(x => reorderBuffer.addItem(x._1, x._2))
    xs
  }

  def tick(): Unit = {
    if (output.isDefined) return
    input match {
      case Some(xs) =>
        reservationStation.input = Some(this.buildReservationStationInput(xs))
        input = None
      case None => ()
    }
    reservationStation.tick()
    val instructions = reservationStation.getNReadyInstructions(executors.count(_.isReady))
    if (instructions.nonEmpty) {
      output = Some(instructions)
    } else {
      output = None
    }
  }

}