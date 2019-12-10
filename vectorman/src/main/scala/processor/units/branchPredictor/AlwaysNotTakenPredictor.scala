package processor.units.branchPredictor
import processor._

class AlwaysNotTakenPredictor extends BranchPredictor {

  private var lastSeenPC: ProgramCounter = -1

  override def predict(): ProgramCounter = {
    val prediction = this.lastSeenPC + 1
    this.predictions += (this.lastSeenPC -> prediction)
    logger.debug(s"Predicting: $prediction")
    prediction
  }

  override def ingest(instruction: String, programCounter: ProgramCounter): Unit = {
    lastSeenPC = programCounter
    logger.debug(s"Ingested: PC:$instruction")
  }
}
